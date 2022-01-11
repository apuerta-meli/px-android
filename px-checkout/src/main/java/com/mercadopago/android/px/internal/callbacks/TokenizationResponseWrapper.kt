package com.mercadopago.android.px.internal.callbacks

import android.net.Uri
import com.mercadopago.android.px.internal.util.JsonUtil.fromJson

private const val RESPONSE = "response"

internal class TokenizationResponseWrapper : DeepLinkWrapper() {

    override fun resolveDeepLink(uri: Uri) {
        uri.getQueryParameter(RESPONSE)?.let {
            val tokenizationResponse = fromJson(it, TokenizationResponse::class.java)!!
            listener.onTokenization(tokenizationResponse.result)
        }
    }
}
