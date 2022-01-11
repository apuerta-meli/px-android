package com.mercadopago.android.px.model.internal.remedies

import com.google.gson.annotations.SerializedName

enum class CardSize {
    @SerializedName ("large") LARGE,
    @SerializedName ("small") SMALL,
    @SerializedName ("xsmall") XSMALL,
    @SerializedName ("mini") MINI
}