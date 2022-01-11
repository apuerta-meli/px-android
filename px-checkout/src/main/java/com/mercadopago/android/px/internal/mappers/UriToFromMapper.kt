package com.mercadopago.android.px.internal.mappers

import android.net.Uri
import com.mercadopago.android.px.internal.callbacks.From
import java.util.Locale

private const val FROM = "from"

internal class UriToFromMapper : Mapper<Uri, From>() {
    override fun map(value: Uri): From {
        val from = value.getQueryParameter(FROM) ?: From.NONE.value
        return From.valueOf(from.toUpperCase(Locale.ROOT))
    }
}
