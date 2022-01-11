package com.mercadopago.android.px.addons

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AuthenticationDefaultBehaviourTest {

    @Test
    fun testAuthenticationDefaultBehaviour() {
        Assert.assertNull(BehaviourProvider.getAuthenticationBehaviour().getDeviceProfileId())
    }
}