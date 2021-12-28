package com.mercadopago.android.px.internal.features.payment_congrats

import com.mercadopago.android.px.configuration.PostPaymentConfiguration
import com.mercadopago.android.px.internal.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModelMapper
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.model.IPaymentDescriptor
import com.mercadopago.android.px.model.Payment

internal class CongratsResultFactory(
    val postPaymentConfiguration: PostPaymentConfiguration,
    private val paymentCongratsModelMapper: PaymentCongratsModelMapper
) {

    fun isValidPostPaymentFlow(iPaymentDescriptor: IPaymentDescriptor) =
        postPaymentConfiguration.getPostPaymentDeepLinkUrl().isNotNullNorEmpty()
                && Payment.StatusCodes.STATUS_APPROVED == iPaymentDescriptor.paymentStatus

    fun create(paymentModel: PaymentModel, redirectUrl: String?): CongratsResult = when {
        postPaymentConfiguration.getPostPaymentDeepLinkUrl().isNotNullNorEmpty() && paymentModel.paymentResult.isApproved ->
            CongratsResult.CongratsPostPaymentResult(paymentModel, postPaymentConfiguration.getPostPaymentDeepLinkUrl())
        redirectUrl.isNotNullNorEmpty() -> CongratsResult.SkipCongratsResult(paymentModel)
        paymentModel is BusinessPaymentModel -> CongratsResult.CongratsBusinessPaymentResult(paymentCongratsModelMapper.map(paymentModel))
        else -> CongratsResult.CongratsPaymentResult(paymentModel)
    }
}
