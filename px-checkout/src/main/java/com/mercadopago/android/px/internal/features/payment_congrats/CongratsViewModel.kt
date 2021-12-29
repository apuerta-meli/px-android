package com.mercadopago.android.px.internal.features.payment_congrats

import androidx.lifecycle.viewModelScope
import com.mercadopago.android.px.internal.base.BaseState
import com.mercadopago.android.px.internal.base.BaseViewModelWithState
import com.mercadopago.android.px.internal.features.checkout.PostPaymentUrlsMapper
import com.mercadopago.android.px.internal.livedata.MediatorSingleLiveData
import com.mercadopago.android.px.internal.repository.CongratsRepository
import com.mercadopago.android.px.internal.repository.DisabledPaymentMethodRepository
import com.mercadopago.android.px.internal.repository.PaymentRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.model.IPaymentDescriptor
import com.mercadopago.android.px.tracking.internal.MPTracker
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class CongratsViewModel(
    private val congratsRepository: CongratsRepository,
    private val paymentRepository: PaymentRepository,
    private val disabledPaymentMethodRepository: DisabledPaymentMethodRepository,
    private val congratsResultFactory: CongratsResultFactory,
    private val paymentSettingRepository: PaymentSettingRepository,
    private val postPaymentUrlsMapper: PostPaymentUrlsMapper,
    tracker: MPTracker
) : BaseViewModelWithState<CongratsViewModel.State>(tracker), CongratsRepository.PostPaymentCallback {

    val congratsResultLiveData = MediatorSingleLiveData<CongratsResult>()

    override fun initState() = State()

    init {
        paymentSettingRepository.advancedConfiguration.postPaymentConfiguration.cleanPostPaymentDeepLinkUrl()
    }

    fun createCongratsResult(
        iPaymentDescriptor: IPaymentDescriptor
    ) {
        viewModelScope.launch {
            delay(3000)
            val paymentResult = paymentRepository.createPaymentResult(iPaymentDescriptor)
            disabledPaymentMethodRepository.handleRejectedPayment(paymentResult)
            congratsRepository.getPostPaymentData(iPaymentDescriptor, paymentResult, this@CongratsViewModel)
        }
    }

    override fun handleResult(paymentModel: PaymentModel) {
        congratsResultLiveData.value = congratsResultFactory.create(
            paymentModel,
            resolvePostPaymentUrls(paymentModel)?.redirectUrl
        )
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
        var iPaymentDescriptor: IPaymentDescriptor? = null
    ) : BaseState
}