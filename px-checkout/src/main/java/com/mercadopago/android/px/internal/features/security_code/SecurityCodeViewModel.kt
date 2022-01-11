package com.mercadopago.android.px.internal.features.security_code

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mercadopago.android.px.internal.base.BaseViewModel
import com.mercadopago.android.px.internal.base.use_case.TokenizeParams
import com.mercadopago.android.px.internal.base.use_case.TokenizeUseCase
import com.mercadopago.android.px.internal.features.pay_button.PayButton
import com.mercadopago.android.px.internal.features.security_code.domain.use_case.DisplayDataUseCase
import com.mercadopago.android.px.internal.features.security_code.domain.use_case.DisplayDataUseCase.CardParams
import com.mercadopago.android.px.internal.features.security_code.domain.use_case.SecurityTrackModelUseCase
import com.mercadopago.android.px.internal.features.security_code.mapper.TrackingParamModelMapper
import com.mercadopago.android.px.internal.features.security_code.model.SecurityCodeDisplayModel
import com.mercadopago.android.px.internal.features.security_code.tracking.SecurityCodeTracker
import com.mercadopago.android.px.internal.mappers.CardUiMapper
import com.mercadopago.android.px.model.Card
import com.mercadopago.android.px.model.PaymentRecovery
import com.mercadopago.android.px.model.internal.PaymentConfiguration
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.model.Reason

internal class SecurityCodeViewModel(
    private val tokenizeUseCase: TokenizeUseCase,
    private val displayDataUseCase: DisplayDataUseCase,
    private val trackModelUseCase: SecurityTrackModelUseCase,
    private val trackParamsMapper: TrackingParamModelMapper,
    private val cardUiMapper: CardUiMapper,
    tracker: MPTracker) : BaseViewModel(tracker) {

    private val displayModelMutableLiveData = MutableLiveData<SecurityCodeDisplayModel>()
    val displayModelLiveData: LiveData<SecurityCodeDisplayModel>
        get() = displayModelMutableLiveData
    private val tokenizeErrorApiMutableLiveData = MutableLiveData<Unit>()
    val tokenizeErrorApiLiveData: LiveData<Unit>
        get() = tokenizeErrorApiMutableLiveData

    private lateinit var paymentConfiguration: PaymentConfiguration
    private lateinit var securityCodeTracker: SecurityCodeTracker
    private lateinit var card: Card
    private lateinit var reason: Reason
    private var paymentRecovery: PaymentRecovery? = null

    fun init(
        paymentConfiguration: PaymentConfiguration,
        card: Card?,
        paymentRecovery: PaymentRecovery?,
        reason: Reason?) {
        this.paymentConfiguration = paymentConfiguration
        this.card = card ?: paymentRecovery?.card ?: error("Card is required for SecurityCode Screen")
        this.paymentRecovery = paymentRecovery
        this.reason = reason ?: paymentRecovery?.let { Reason.from(it) }
            ?: error("PaymentRecovery or Reason are required for SecurityCode Screen")

        trackModelUseCase.execute(trackParamsMapper.map(this.card, this.reason),
            success = { tracker ->
                securityCodeTracker = tracker
                securityCodeTracker.trackSecurityCode()
            })

        val cardParams = with(this.card) {
            CardParams(id,
                paymentMethod?.displayInfo?.cvvInfo,
                getSecurityCodeLength(),
                getSecurityCodeLocation())
        }

        displayDataUseCase.execute(cardParams,
            success = { displayData ->
                displayModelMutableLiveData.value = SecurityCodeDisplayModel(
                    displayData.title,
                    displayData.message,
                    displayData.securityCodeLength,
                    displayData.cardDisplayInfo?.let(cardUiMapper::map)
                )
            })
    }

    fun onBack() {
        securityCodeTracker.trackAbortSecurityCode()
    }

    fun onPaymentError() {
        securityCodeTracker.trackPaymentApiError()
    }

    fun handlePrepayment(callback: PayButton.OnReadyForPaymentCallback) {
        securityCodeTracker.trackConfirmSecurityCode()
        callback.call(paymentConfiguration)
    }

    fun enqueueOnExploding(cvv: String, callback: PayButton.OnEnqueueResolvedCallback) {
        tokenizeUseCase.execute(TokenizeParams(cvv, card, paymentRecovery),
            success = { callback.success() },
            failure = {
                securityCodeTracker.trackTokenApiError()
                tokenizeErrorApiMutableLiveData.value = Unit
                callback.failure()
            })
    }

    fun onGetViewTrackPath(callback: PayButton.ViewTrackPathCallback) {
        callback.call(securityCodeTracker.getViewTrackPath())
    }
}
