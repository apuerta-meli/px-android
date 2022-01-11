package com.mercadopago.android.px.addons.tokenization

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TokenizationResponse(
    val result: State,
    val resultErrorType: ErrorType? = null
) : Parcelable {

    enum class State {
        @SerializedName("success") SUCCESS,
        @SerializedName("pending") PENDING,
        @SerializedName("error") ERROR
    }

    enum class ErrorType {
        @SerializedName("recoverable") RECOVERABLE,
        @SerializedName("non_recoverable") NON_RECOVERABLE
    }
}
