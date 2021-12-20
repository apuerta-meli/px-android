package com.mercadopago.android.px.addons.internal

import com.mercadopago.android.px.addons.TokenDeviceBehaviour
import com.mercadopago.android.px.addons.tokenization.Tokenize

internal class TokenDeviceDefaultBehaviour : TokenDeviceBehaviour {
    override val isFeatureAvailable: Boolean = false
    override fun getTokenize(flowId: String, cardId: String) = Tokenize()
}
