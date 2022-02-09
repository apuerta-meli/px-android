package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

internal class RemedyModalView(
    payerPaymentMethodRepository: PayerPaymentMethodRepository,
    userSelectionRepository: UserSelectionRepository
) : TrackWrapper() {

    private val remedyModalViewModel = RemedyModalViewModel(payerPaymentMethodRepository, userSelectionRepository)

    override fun getTrack() = TrackFactory.withView(PATH).addData(getData()).build()

    private fun getData(): Map<String, Any> {
        return remedyModalViewModel.toMap()
    }

    companion object {
        private const val PATH = "$BASE_PATH/result/error/remedy/modal"
    }
}