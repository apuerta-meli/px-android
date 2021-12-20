package com.mercadopago.android.px.addons

import org.junit.Assert
import org.junit.Test

class TokenDeviceDefaultBehaviourTest {

    private val tokenDeviceBehaviour = BehaviourProvider.getTokenDeviceBehaviour()

    @Test
    fun `when check feature availability then return false`() {
        Assert.assertFalse(tokenDeviceBehaviour.isFeatureAvailable)
    }
}
