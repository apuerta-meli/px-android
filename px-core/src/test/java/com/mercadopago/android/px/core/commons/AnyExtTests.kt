package com.mercadopago.android.px.core.commons

import com.mercadopago.android.px.core.commons.extensions.notNull
import com.mercadopago.android.px.core.commons.extensions.runIfNotNull
import com.mercadopago.android.px.core.commons.extensions.runIfNull
import com.mercadopago.android.px.core.utils.CallbackTest
import org.junit.Assert.assertEquals
import java.lang.IllegalStateException
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verifyNoInteractions

@RunWith(MockitoJUnitRunner::class)
class AnyExtTests {

    @Mock
    private lateinit var testFunction: CallbackTest<String?>

    @Test
    fun notNullShouldReturnValueWhenNotNull() {
        val test = "Not null test"
        assertEquals(test.notNull(), test)
    }

    @Test(expected = IllegalStateException::class)
    fun notNullShouldThrowIllegalStateExceptionWhenNull() {
        val test: String? = null
        test.notNull()
    }

    @Test
    fun runIfNotNullShouldRunWhenNotNull() {
        val test = "Not null run"
        test.runIfNotNull { testFunction(it) }
        verify(testFunction).invoke(test)
    }

    @Test
    fun runIfNotNullShouldNotRunWhenNull() {
        val test: String? = null
        test.runIfNotNull { testFunction(it) }
        verifyNoInteractions(testFunction)
    }

    @Test
    fun runIfNullShouldNotRunWhenNotNull() {
        val test = "Not null run"
        test.runIfNull { testFunction(null) }
        verifyNoInteractions(testFunction)
    }

    @Test
    fun runIfNullShouldRunWhenNull() {
        val test: String? = null
        test.runIfNull { testFunction(null) }
        verify(testFunction).invoke(null)
    }
}
