package com.mercadopago.android.px.core.presentation

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.mercadopago.android.px.core.presentation.extensions.hideKeyboard
import com.mercadopago.android.px.core.presentation.extensions.showKeyboard
import com.mercadopago.android.px.core.utils.BasicRobolectricTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Answers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class KeyboardExtTests: BasicRobolectricTest() {

    @Mock(answer = Answers.RETURNS_MOCKS)
    private lateinit var contextMock: Context

    @Test
    fun showKeyboardTest() {
        val inputManager = mock<InputMethodManager>()
        whenever(contextMock.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn(inputManager)
        with(View(contextMock)) {
            showKeyboard()
            Mockito.verify(inputManager).showSoftInput(findFocus(), InputMethodManager.SHOW_IMPLICIT)
        }
    }

    @Test
    fun hideKeyboardTest() {
        val inputManager = mock<InputMethodManager>()
        whenever(contextMock.getSystemService(Context.INPUT_METHOD_SERVICE)).thenReturn(inputManager)
        with(View(contextMock)) {
            hideKeyboard()
            Mockito.verify(inputManager).toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
    }
}