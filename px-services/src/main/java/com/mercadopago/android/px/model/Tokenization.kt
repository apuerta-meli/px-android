package com.mercadopago.android.px.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tokenization(
    @SerializedName("last_modified_date")
    val lastModifiedDate: String? = null,

    @SerializedName("tokenization_id")
    val tokenizationId: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("binding_tokenization_id")
    val bindingTokenizationId: String? = null
) : Parcelable

object  BindingInfo {
    var bindingTokenizationId: String? = null
    var bindingTokenizationStatus: String? = null
}