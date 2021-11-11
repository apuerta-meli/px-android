package com.mercadopago.android.px.model.internal

import com.google.gson.annotations.SerializedName

enum class CheckoutType {
    @SerializedName("regular") CUSTOM_REGULAR,
    @SerializedName("default_regular") DEFAULT_REGULAR,
    @SerializedName("scheduled") CUSTOM_SCHEDULED
}