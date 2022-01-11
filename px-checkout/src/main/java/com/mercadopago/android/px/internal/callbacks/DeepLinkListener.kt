package com.mercadopago.android.px.internal.callbacks

internal interface DeepLinkListener {
    fun onDefault()
    fun onTokenization(state: TokenizationResponse.State)
}
