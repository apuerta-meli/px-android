package com.mercadopago.android.px.internal.features.payment_congrats

import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel
import com.mercadopago.android.px.internal.viewmodel.PaymentModel

internal open class CongratsResult {
    data class CongratsPaymentResult(val paymentModel: PaymentModel) : CongratsResult()
    data class CongratsBusinessPaymentResult(val paymentCongratsModel: PaymentCongratsModel) : CongratsResult()
    data class SkipCongratsResult(val paymentModel: PaymentModel) : CongratsResult()
    data class CongratsPostPaymentResult(val paymentModel: PaymentModel, val postPaymentDeepLinkUrl: String) : CongratsResult()
}
