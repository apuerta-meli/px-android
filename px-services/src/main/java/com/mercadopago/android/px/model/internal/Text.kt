package com.mercadopago.android.px.model.internal

import android.os.Parcelable
import java.io.Serializable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Text(
    val message: String,
    val backgroundColor: String,
    val textColor: String,
    val weight: String
) : Parcelable, Serializable
