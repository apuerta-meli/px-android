package com.mercadopago.android.px.internal.callbacks

import android.net.Uri

internal class DefaultWrapper : DeepLinkWrapper() {

    override fun resolveDeepLink(uri: Uri) {
        listener.onDefault()
    }
}
