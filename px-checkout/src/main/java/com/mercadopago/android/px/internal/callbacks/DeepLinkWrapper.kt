package com.mercadopago.android.px.internal.callbacks

import android.net.Uri

internal abstract class DeepLinkWrapper {
    lateinit var listener: DeepLinkListener
    abstract fun resolveDeepLink(uri: Uri)
}
