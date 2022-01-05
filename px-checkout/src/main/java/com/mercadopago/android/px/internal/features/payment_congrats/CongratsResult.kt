package com.mercadopago.android.px.internal.features.payment_congrats

import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel
import com.mercadopago.android.px.internal.viewmodel.PaymentModel

internal sealed class CongratsResult

internal open class BaseCongratsResult : CongratsResult() {
    data class PaymentResult(val paymentModel: PaymentModel) : BaseCongratsResult()
    data class BusinessPaymentResult(val paymentCongratsModel: PaymentCongratsModel) : BaseCongratsResult()
}

internal open class CongratsPaymentResult : BaseCongratsResult() {
    data class SkipCongratsResult(val paymentModel: PaymentModel) : CongratsPaymentResult()
}

internal open class CongratsPostPaymentResult : BaseCongratsResult() {
    data class Loading(val isLoading: Boolean) : CongratsPostPaymentResult()
    object ConnectionError : CongratsPostPaymentResult()
    object BusinessError : CongratsPostPaymentResult()
}
