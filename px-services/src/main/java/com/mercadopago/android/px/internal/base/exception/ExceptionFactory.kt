package com.mercadopago.android.px.internal.base.exception

import com.mercadopago.android.px.internal.core.extensions.orIfEmpty
import com.mercadopago.android.px.internal.util.ApiUtil
import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.exceptions.SocketTimeoutApiException
import retrofit2.HttpException

internal object ExceptionFactory {

    fun connectionError() = ApiException().also {
        it.status = ApiUtil.StatusCodes.NO_CONNECTIVITY_ERROR
        it.message = "No connection"
    }

    fun genericError(message: String? = null) = ApiException().also { it.message = message.orEmpty() }

    fun httpError(throwable: HttpException) = JsonUtil.fromJson(
        throwable.response()?.errorBody()?.string(),
        ApiException::class.java
    )!!

    fun socketTimeoutError(message: String? = null) = SocketTimeoutApiException(message.orIfEmpty("Socket timeout"))
}
