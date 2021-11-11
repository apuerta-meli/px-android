package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.tracking.internal.TrackFactory
import com.mercadopago.android.px.tracking.internal.TrackWrapper

internal class PayButtonPressedEvent(private val viewTrackPath: String) : TrackWrapper() {
    override fun getTrack() = TrackFactory.withEvent("$viewTrackPath/pay_button_pressed").build()
}
