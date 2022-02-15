package com.mercadopago.android.px.model.internal

import android.view.Gravity
import com.google.gson.annotations.SerializedName

enum class TextAlignment {
    @SerializedName("left") LEFT,
    @SerializedName("center") CENTER,
    @SerializedName("right") RIGHT;
}

fun TextAlignment?.toGravity(): Int = when (this) {
    TextAlignment.CENTER -> Gravity.CENTER_HORIZONTAL
    TextAlignment.RIGHT -> Gravity.END
    TextAlignment.LEFT -> Gravity.START
    else -> Gravity.START
}
