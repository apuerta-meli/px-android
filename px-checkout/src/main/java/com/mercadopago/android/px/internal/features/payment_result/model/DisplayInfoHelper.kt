package com.mercadopago.android.px.internal.features.payment_result.model

import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsText
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentInfo
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentResultExtraInfo
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentResultInfo
import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.model.PaymentData
import com.mercadopago.android.px.model.display_info.CustomSearchItemDisplayInfo.Result.ExtraInfo
import com.mercadopago.android.px.model.display_info.CustomSearchItemDisplayInfo.Result.PaymentMethod
import com.mercadopago.android.px.model.display_info.DisplayInfo

internal class DisplayInfoHelper(
    val payerPaymentMethodRepository: PayerPaymentMethodRepository,
    val userSelectionRepository: UserSelectionRepository
) {

    fun resolve(paymentData: PaymentData, paymentInfoBuilder: PaymentInfo.Builder) {
        val payerPaymentMethod = userSelectionRepository.customOptionId?.let { payerPaymentMethodRepository[it] }
        if (payerPaymentMethod?.displayInfo?.result?.paymentMethod != null) {
            resolveCustomSearchItemPaymentMethodDisplayInfo(
                payerPaymentMethod.displayInfo!!.result.paymentMethod!!,
                paymentInfoBuilder
            )
        }
        else {
            resolveGenericPaymentMethodDisplayInfo(paymentData.paymentMethod.displayInfo, paymentInfoBuilder)
        }
        payerPaymentMethod?.displayInfo?.result?.extraInfo?.let {
            resolveCustomSearchItemExtraInfo(it, paymentInfoBuilder)
        }
    }

    private fun resolveCustomSearchItemExtraInfo(
        extraInfo: ExtraInfo,
        paymentInfoBuilder: PaymentInfo.Builder
    ) {
        paymentInfoBuilder.withExtraInfo(
            PaymentResultExtraInfo(extraInfo.detail.map {
                PaymentCongratsText.from(it)!!
            })
        )
    }

    private fun resolveGenericPaymentMethodDisplayInfo(
        displayInfo: DisplayInfo?,
        paymentInfoBuilder: PaymentInfo.Builder
    ) {
        displayInfo?.let {
            val paymentResultInfo = PaymentResultInfo(it.resultInfo.title, it.resultInfo.subtitle)
            paymentInfoBuilder.withConsumerCreditsInfo(paymentResultInfo)
            val description = it.description
            if (description != null) {
                val descriptionText = PaymentCongratsText.from(description)
                paymentInfoBuilder.withPaymentMethodDescriptionText(descriptionText)
            }
        }
    }

    private fun resolveCustomSearchItemPaymentMethodDisplayInfo(
        paymentMethod: PaymentMethod,
        paymentInfoBuilder: PaymentInfo.Builder
    ) {
        paymentInfoBuilder
            .withDescriptionText(PaymentCongratsText.from(paymentMethod.detail.getOrNull(0)))
            .withPaymentMethodDescriptionText(PaymentCongratsText.from(paymentMethod.detail.getOrNull(1)))
            .withStatementText(PaymentCongratsText.from(paymentMethod.detail.getOrNull(2)))
            .withIconUrl(paymentMethod.iconUrl)
    }
}
