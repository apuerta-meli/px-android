package com.mercadopago.android.px.core.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.mercadopago.android.px.core.R
import org.junit.After
import org.junit.Before
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

open class BasicRobolectricTest {
    private var closable: AutoCloseable? = null

    @Before
    fun initMocks() {
        closable = MockitoAnnotations.openMocks(this)
    }

    @After
    fun tearDown() {
        closable?.close()
        closable = null
        Mockito.validateMockitoUsage()
    }

    protected fun getContext(): Context = ApplicationProvider.getApplicationContext<Context>().also {
        it.setTheme(R.style.Theme_AppCompat)
    }
}
