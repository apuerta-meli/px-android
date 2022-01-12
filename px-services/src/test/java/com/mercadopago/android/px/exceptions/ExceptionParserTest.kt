package com.mercadopago.android.px.exceptions

import com.mercadopago.android.px.internal.base.exception.ExceptionParser
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.exceptions.SocketTimeoutApiException
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.internal.matchers.apachecommons.ReflectionEquals
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.HttpException
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@RunWith(MockitoJUnitRunner::class)
class ExceptionParserTest {

    @Test
    fun whenHttpExceptionThenItShouldReturnApiExceptionWithResponseBodyData() {
        val apiException = ApiException().also {
            it.message = "test"
            it.error = "error"
            it.status = 400
        }
        val ex = HttpException(
            Response.error<Any>(400, ResponseBody.create(
            null, "{ 'message':'${apiException.message}', " +
                "'error':'${apiException.error}', " +
                "'status':${apiException.status} }")))
        val result = ExceptionParser.parse(ex)
        assertTrue(ReflectionEquals(apiException).matches(result))
    }

    @Test
    fun whenUnknownHostExceptionThenItShouldReturnApiExceptionWithConnectionMessage() {
        val ex = UnknownHostException()
        val result = ExceptionParser.parse(ex)
        assertEquals("No connection", result.message)
    }

    @Test
    fun whenExIsNotOnTheListOfRecognizedExsAndHasMessageThenItShouldReturnGenericApiExceptionWithMessage() {
        val msg = "test msg"
        val ex = Exception(msg)
        val result = ExceptionParser.parse(ex)
        assertEquals(msg, result.message)
    }

    @Test
    fun whenExIsNotOnTheListOfRecognizedExsAndHasNoMessageThenItShouldReturnGenericApiExceptionWithNoMessage() {
        val ex = Exception()
        val result = ExceptionParser.parse(ex)
        assertEquals("", result.message)
    }

    @Test
    fun whenSocketTimeoutExceptionThenItShouldReturnSocketTimeoutApiException() {
        val msg = "test msg"
        val ex = SocketTimeoutException(msg)
        val result = ExceptionParser.parse(ex)
        assert(result is SocketTimeoutApiException)
        assertEquals(msg, result.message)
    }

    @Test
    fun whenSocketTimeoutExceptionWithNoMsgThenItShouldReturnSocketTimeoutApiExceptionWithDefaultMsg() {
        val ex = SocketTimeoutException()
        val result = ExceptionParser.parse(ex)
        assert(result is SocketTimeoutApiException)
        assertEquals("Socket timeout", result.message)
    }
}
