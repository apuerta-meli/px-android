package com.mercadopago.android.px.internal.features.payment_result.mappers

import com.mercadopago.android.px.internal.features.payment_result.remedies.RemediesModel
import com.mercadopago.android.px.model.internal.remedies.CardSize
import com.mercadopago.android.px.internal.features.payment_result.remedies.view.CvvRemedy
import com.mercadopago.android.px.internal.features.payment_result.remedies.view.HighRiskRemedy
import com.mercadopago.android.px.internal.features.payment_result.remedies.view.RetryPaymentFragment
import com.mercadopago.android.px.internal.mappers.Mapper
import com.mercadopago.android.px.model.internal.remedies.CvvRemedyResponse
import com.mercadopago.android.px.model.internal.remedies.RemediesResponse

internal object PaymentResultRemediesModelMapper : Mapper<RemediesResponse, RemediesModel>() {
    override fun map(response: RemediesResponse): RemediesModel {
        var title = ""
        val retryPaymentModel = response.suggestedPaymentMethod?.let {
            title = it.title
            RetryPaymentFragment.Model(response.cvv?.run { message } ?: it.message,
                true, it.alternativePaymentMethod.cardSize, getCvvModel(response.cvv), it.bottomMessage)
        } ?: response.cvv?.let {
            title = it.title
            RetryPaymentFragment.Model(it.message, false, CardSize.SMALL, getCvvModel(it))
        }
        val highRiskModel = response.highRisk?.let {
            title = it.title
            HighRiskRemedy.Model(it.title, it.message, it.deepLink)
        }
        return RemediesModel(title, retryPaymentModel, highRiskModel, response.trackingData)
    }

    private fun getCvvModel(cvvResponse: CvvRemedyResponse?) =
        cvvResponse?.fieldSetting?.run {
            CvvRemedy.Model(hintMessage, title, length)
        }
}