package com.mercadopago.android.px.core.data.extensions

import com.mercadopago.android.px.core.data.network.Response

/*
    Response.Failure<F> with F as Generic extensions
    Those are extensions that does not have any restrictions on F class
*/

/**
 * Applies a transformation when Response is success.
 */
fun <T, F, R> Response<T, F>.map(transform: (T) -> R): Response<R,F> {
    return when (this) {
        is Response.Success -> success(transform(result))
        is Response.Failure -> failure(exception)
    }
}

/**
 * Executes success on result and then complete when Response is success or error on result when Response is Failure.
 *
 * @param complete Function to execute after [success]
 * @param success Function to execute on result if Response is success
 * @param error Function to execute if Response is failure
 */
fun <T, F> Response<T, F>.fold(
    complete: (Unit) -> Unit = { },
    success: (value: T) -> Unit = { },
    error: (error: F) -> Unit = { }
) {
    when (this) {
        is Response.Success -> {
            success(result); complete(Unit)
        }
        is Response.Failure -> error(exception)
    }
}

/**
 * Executes [block] if failure
 *
 * @param block Function to execute if Response is failure
 * @return result on success or null on failure
 */
fun <T, F> Response<T, F>.getOrElse(block: (failure: F) -> Unit): T? {
    val value = getOrNull()
    if (value == null) {
        block((this as Response.Failure).exception)
    }
    return value
}

/**
 * @return result on success or null on failure
 */
fun <T, F> Response<T, F>?.getOrNull(): T? {
    return when (this) {
        is Response.Success -> result
        else -> null
    }
}

/**
 * Applies [block] on result if success
 *
 * @param block Function to execute if success
 * @return Response transformed by block if success or unmodified Response if failure
 */
inline fun <T, F, R> Response<T, F>.next(
    block: (value: T) -> Response<R, F>
): Response<R, F> {
    return when (this) {
        is Response.Success -> block(result)
        is Response.Failure -> failure(exception)
    }
}

/**
 * Executes [block] on result if success
 *
 * @param block Function to execute if success
 */
inline fun <T, F> Response<T, F>.ifSuccess(
    block: (value: T) -> Unit
): Response<T, F> {
    return when (this) {
        is Response.Success -> {
            block(result)
            success(result)
        }
        is Response.Failure -> failure(exception)
    }
}

/*
 Response.Failure<F> with F as Throwable extensions
 Those are extensions that only works when F is throwable
*/

/**
 * Returns result if success or throws exception if failure
 */
fun <T> Response<T, Throwable>?.getOrThrow(): T {
    return when (this) {
        is Response.Success -> result
        is Response.Failure -> throw exception
        else -> throw IllegalStateException("Malformed response")
    }
}
