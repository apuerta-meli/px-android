package com.mercadopago.android.px.internal.callbacks

import android.net.Uri
import com.mercadopago.android.px.internal.mappers.UriToDeepLinkWrapperMapper

internal class DeepLinkHandler(
    private val uriToDeepLinkWrapperMapper: UriToDeepLinkWrapperMapper
) {

    lateinit var deepLinkListener: DeepLinkListener

    fun resolveDeepLink(uri: Uri) {
        with(uriToDeepLinkWrapperMapper.map(uri)) {
            listener = deepLinkListener
            resolveDeepLink(uri)
        }
    }
}
