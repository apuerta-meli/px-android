package com.mercadopago.android.px.core.utils

internal interface CallbackTest<T> : (T) -> Unit {
    override operator fun invoke(value: T)
}