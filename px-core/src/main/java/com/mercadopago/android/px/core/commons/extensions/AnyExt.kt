package com.mercadopago.android.px.core.commons.extensions

fun Any?.runIfNull(action: () -> Unit) {
    if (this == null) {
        action.invoke()
    }
}

inline fun <reified T: Any> T?.notNull() = checkNotNull(this, {"${T::class.java.simpleName} should not be null"})

inline fun <T : Any?, R> T?.runIfNotNull(action: (T) -> R): R? = this?.run { action(this) }
