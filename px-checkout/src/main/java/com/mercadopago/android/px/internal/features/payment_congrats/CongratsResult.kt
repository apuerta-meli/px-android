package com.mercadopago.android.px.internal.features.payment_congrats

import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel
import com.mercadopago.android.px.internal.viewmodel.PaymentModel

internal open class CongratsResult {
    data class CongratsPaymentResult(val paymentModel: PaymentModel) : CongratsResult()
    data class CongratsBusinessPaymentResult(val paymentCongratsModel: PaymentCongratsModel) : CongratsResult()
    data class SkipCongratsResult(val paymentModel: PaymentModel) : CongratsResult()
    data class CongratsPostPaymentResult(val paymentModel: PaymentModel, val postPaymentDeepLinkUrl: String) : CongratsResult()
    data class Loading(val isLoading: Boolean) : CongratsResult()
    object ConnectionError : CongratsResult() {
        val message = R.string.px_no_connection_message
    }
    object BusinessError : CongratsResult()
}
