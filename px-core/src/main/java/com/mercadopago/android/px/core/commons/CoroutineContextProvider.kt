@file:Suppress("unused")

package com.mercadopago.android.px.core.commons

import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

@Suppress("PropertyName")
open class CoroutineContextProvider {
    open val Main: CoroutineContext by lazy { Dispatchers.Main }
    open val IO: CoroutineContext by lazy { Dispatchers.IO }
    open val Default: CoroutineContext by lazy { Dispatchers.Default }
}