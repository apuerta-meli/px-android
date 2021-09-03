package com.mercadopago.android.px.addons

import com.mercadopago.android.px.addons.tokenization.Tokenize

interface TokenDeviceBehaviour {
    val isFeatureAvailable: Boolean
    fun getTokenize(flowId: String, cardId: String): Tokenize
}
