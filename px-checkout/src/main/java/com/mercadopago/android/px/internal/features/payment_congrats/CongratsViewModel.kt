package com.mercadopago.android.px.internal.features.payment_congrats

import androidx.lifecycle.viewModelScope
import com.mercadopago.android.px.internal.base.BaseState
import com.mercadopago.android.px.internal.base.BaseViewModelWithState
import com.mercadopago.android.px.internal.core.ConnectionHelper
import com.mercadopago.android.px.internal.features.checkout.PostPaymentUrlsMapper
import com.mercadopago.android.px.internal.livedata.MediatorSingleLiveData
import com.mercadopago.android.px.internal.repository.CongratsRepository
import com.mercadopago.android.px.internal.repository.PaymentRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.model.IPaymentDescriptor
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.events.NoConnectionFrictionTracker
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class CongratsViewModel(
    private val congratsRepository: CongratsRepository,
    private val paymentRepository: PaymentRepository,
    private val congratsResultFactory: CongratsResultFactory,
    private val paymentSettingRepository: PaymentSettingRepository,
    private val postPaymentUrlsMapper: PostPaymentUrlsMapper,
    private val connectionHelper: ConnectionHelper,
    tracker: MPTracker
) : BaseViewModelWithState<CongratsViewModel.State>(tracker), CongratsRepository.PostPaymentCallback {

    val congratsResultLiveData = MediatorSingleLiveData<CongratsResult>()

    override fun initState() = State()

    init {
        paymentSettingRepository.advancedConfiguration.postPaymentConfiguration.cleanPostPaymentDeepLinkUrl()
    }

    fun createCongratsResult(iPaymentDescriptor: IPaymentDescriptor?) = viewModelScope.launch {
        congratsResultLiveData.value = CongratsResult.Loading(true)
        delay(3000)
        if (connectionHelper.hasConnection()) {
            val descriptor = iPaymentDescriptor ?: paymentRepository.payment
            if (descriptor != null) {
                val paymentResult = paymentRepository.createPaymentResult(descriptor)
                congratsRepository.getPostPaymentData(descriptor, paymentResult, this@CongratsViewModel)
            } else {
                congratsResultLiveData.value = CongratsResult.Loading(false)
                congratsResultLiveData.value = CongratsResult.BusinessError
            }
        } else {
            congratsResultLiveData.value = CongratsResult.Loading(false)
            manageNoConnection()
        }
    }

    override fun handleResult(paymentModel: PaymentModel) {
        congratsResultLiveData.value = CongratsResult.Loading(false)
        congratsResultLiveData.value = congratsResultFactory.create(
            paymentModel,
            resolvePostPaymentUrls(paymentModel)?.redirectUrl
        )
    }

    private fun manageNoConnection() {
        trackNoConnectionFriction()
        congratsResultLiveData.value = CongratsResult.ConnectionError
    }

    private fun trackNoConnectionFriction() {
        track(NoConnectionFrictionTracker)
    }

    private fun resolvePostPaymentUrls(paymentModel: PaymentModel): PostPaymentUrlsMapper.Response? {
        return paymentSettingRepository.checkoutPreference?.let { preference ->
            val congratsResponse = paymentModel.congratsResponse
            postPaymentUrlsMapper.map(
                PostPaymentUrlsMapper.Model(
                congratsResponse.redirectUrl,
                congratsResponse.backUrl,
                paymentModel.payment,
                preference,
                paymentSettingRepository.site.id
            ))
        }
    }

    @Parcelize
    data class State(
        var iPaymentDescriptor: IPaymentDescriptor? = null,
        var retryCounter: Int = 0
    ) : BaseState
}