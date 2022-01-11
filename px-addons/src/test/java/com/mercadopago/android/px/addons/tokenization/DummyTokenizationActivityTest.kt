package com.mercadopago.android.px.addons.tokenization

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DummyTokenizationActivityTest {

    @Test
    fun `when dummy tokenization activity is started then returns response`() {
        val scenario = ActivityScenario.launch(DummyTokenizationActivity::class.java)

        scenario.result.let {
            Assert.assertTrue(it.resultData.extras?.containsKey(TOKENIZATION_RESPONSE) == true)
        }
    }
}
