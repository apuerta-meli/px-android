package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

internal class RemedyModalView(private val bankInfoModalViewModel: BankInfoModalViewModel) : TrackWrapper() {

    override fun getTrack() = TrackFactory.withView(PATH).addData(getData()).build()

    private fun getData(): Map<String, Any> {
        return bankInfoModalViewModel.toMap()
    }

    companion object {
        private const val PATH = "$BASE_PATH/result/error/remedy/modal"
    }
}