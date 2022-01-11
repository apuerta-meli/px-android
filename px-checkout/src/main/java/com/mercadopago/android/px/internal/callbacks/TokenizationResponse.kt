package com.mercadopago.android.px.internal.callbacks

import com.google.gson.annotations.SerializedName

internal data class TokenizationResponse(
    val result: State,
    val resultErrorType: ErrorType? = null
) {

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
