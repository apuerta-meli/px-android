package com.mercadopago.android.px.core.commons

import com.mercadopago.android.px.core.commons.extensions.EMPTY
import com.mercadopago.android.px.core.commons.extensions.format
import com.mercadopago.android.px.core.commons.extensions.ifNotEmpty
import com.mercadopago.android.px.core.commons.extensions.isEmpty
import com.mercadopago.android.px.core.commons.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.core.commons.extensions.orIfEmpty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

@RunWith(MockitoJUnitRunner::class)
class StringExtTests {

    @Mock
    private lateinit var callbackMock: (String) -> Unit

    @Test
    fun isNotNullNorEmptyShouldReturnTrueWhenNotNullNorEmpty() {
        val testString = "test"
        assertTrue(testString.isNotNullNorEmpty())
    }

    @Test
    fun isNotNullNorEmptyShouldReturnFalseWhenNull() {
        val testString: String? = null
        assertFalse(testString.isNotNullNorEmpty())
    }

    @Test
    fun isNotNullNorEmptyShouldReturnFalseWhenEmpty() {
        val testString = EMPTY
        assertFalse(testString.isNotNullNorEmpty())
    }

    @Test
    fun isEmptyShouldReturnTrueWhenEmpty() {
        val testString = EMPTY
        assertTrue(testString.isEmpty())
    }

    @Test
    fun isEmptyShouldReturnFalseWhenNotEmpty() {
        val testString = "test"
        assertFalse(testString.isEmpty())
    }

    @Test
    fun ifNotEmptyShouldExecuteFunctionWhenNotEmpty() {
        val testString = "test"
        testString.ifNotEmpty { callbackMock("123") }
        verify(callbackMock).invoke("123")
    }

    @Test
    fun ifNotEmptyShouldNotExecuteFunctionWhenEmpty() {
        val testString = EMPTY
        testString.ifNotEmpty { callbackMock("123") }
        verifyNoInteractions(callbackMock)
    }

    @Test
    fun orIfEmptyShouldReturnCurrentObjectWhenNotEmpty() {
        val testString = "test"
        assertEquals(testString, testString.orIfEmpty("123"))
    }

    @Test
    fun orIfEmptyShouldReturnFallbackWhenEmpty() {
        val testString = EMPTY
        assertEquals("123", testString.orIfEmpty("123"))
    }

    @Test
    fun stringFormatShouldReturnReplacedString() {
        val stringToReplace = "Hola {0}, como estas {1}?"
        val name = "Juan Carlos"
        val characteristic = "buen hombre"
        val replacedString = stringToReplace.format(name, characteristic)
        assertEquals("Hola $name, como estas $characteristic?", replacedString)
    }

    @Test(expected = IllegalArgumentException::class)
    fun whenArgsDoesNotMatchWithExpectedParametersThenThrowException() {
        val stringToReplace = "Hola {0}, como estas {1}?"
        val name = "Juan Carlos"
        stringToReplace.format(name)
    }
}
