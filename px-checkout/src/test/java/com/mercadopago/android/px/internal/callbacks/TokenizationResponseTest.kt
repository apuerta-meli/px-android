package com.mercadopago.android.px.internal.callbacks

import android.net.Uri
import com.mercadopago.android.px.BasicRobolectricTest
import com.mercadopago.android.px.internal.util.JsonUtil.fromJson
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TokenizationResponseTest : BasicRobolectricTest() {

    @Test
    fun whenReceiveIntentWithDataThenReturnStateSuccessWithMessage() {
        val uri: Uri = Uri.parse("mercadopago://px/one_tap?from=tokenization&response={\"result\":\"success\"}")
        val response: String = uri.getQueryParameter("response")!!
        val tokenizationResponse: TokenizationResponse = fromJson(response, TokenizationResponse::class.java)!!

        assertEquals(TokenizationResponse.State.SUCCESS, tokenizationResponse.result)
    }

    @Test
    fun whenReceiveIntentWithDataThenReturnStatePendingWithMessage() {
        val uri: Uri = Uri.parse("mercadopago://px/one_tap?from=tokenization&response={\"result\":\"pending\"}")
        val response: String = uri.getQueryParameter("response")!!
        val tokenizationResponse: TokenizationResponse = fromJson(response, TokenizationResponse::class.java)!!

        assertEquals(TokenizationResponse.State.PENDING, tokenizationResponse.result)
    }

    @Test
    fun whenReceiveIntentWithDataThenReturnStateErrorWithMessage() {
        val uri: Uri = Uri.parse("mercadopago://px/one_tap?from=tokenization&response={\"result\":\"error\"}")
        val response: String = uri.getQueryParameter("response")!!
        val tokenizationResponse: TokenizationResponse = fromJson(response, TokenizationResponse::class.java)!!

        assertEquals(TokenizationResponse.State.ERROR, tokenizationResponse.result)
    }

    @Test
    fun whenReceiveIntentWithDataThenReturnFromTokenization() {
        val uri: Uri = Uri.parse("mercadopago://px/one_tap?from=tokenization&response={\"result\":\"error\"}")
        val from: String = uri.getQueryParameter("from")!!

        assertEquals(From.TOKENIZATION.value, from)
    }

    @Test
    fun whenReceiveIntentWithDataThenReturnFromDefault() {
        val uri: Uri = Uri.parse("mercadopago://px/one_tap?from=none&response={\"result\":\"error\"}")
        val from: String = uri.getQueryParameter("from")!!

        assertEquals(From.NONE.value, from)
    }
}
