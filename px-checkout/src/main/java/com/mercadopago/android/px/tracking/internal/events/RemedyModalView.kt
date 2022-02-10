package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

internal class RemedyModalView(
    customOptionId: String,
    payerPaymentMethodRepository: PayerPaymentMethodRepository
) : TrackWrapper() {

    private val bankInfoModel = BankInfoModel(customOptionId, payerPaymentMethodRepository)

    override fun getTrack() = TrackFactory.withView(PATH).addData(getData()).build()

    private fun getData(): Map<String, Any> {
        return bankInfoModel.toMap()
    }

    companion object {
        private const val PATH = "$BASE_PATH/result/error/remedy/modal"
    }
}