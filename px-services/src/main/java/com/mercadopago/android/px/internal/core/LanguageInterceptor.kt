package com.mercadopago.android.px.internal.core

import com.mercadopago.android.px.addons.LocaleBehaviour
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class LanguageInterceptor(private val localeBehaviour: LocaleBehaviour) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val request = originalRequest.newBuilder()
            .header(LANGUAGE_HEADER, localeBehaviour.locale.toLanguageTag())
            .build()
        return chain.proceed(request)
    }

    companion object {
        private const val LANGUAGE_HEADER = "Accept-Language"
    }
}
