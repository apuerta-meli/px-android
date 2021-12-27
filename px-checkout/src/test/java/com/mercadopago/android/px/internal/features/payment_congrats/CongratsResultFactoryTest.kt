package com.mercadopago.android.px.internal.features.payment_congrats

import com.mercadopago.android.px.assertEquals
import com.mercadopago.android.px.configuration.PostPaymentConfiguration
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModelMapper
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
internal class CongratsResultFactoryTest {

    private lateinit var congratsResultFactory: CongratsResultFactory

    @Mock
    private lateinit var postPaymentConfiguration: PostPaymentConfiguration
    @Mock
    private lateinit var paymentCongratsModelMapper: PaymentCongratsModelMapper

    @Before
    fun setUp() {
        congratsResultFactory = CongratsResultFactory(postPaymentConfiguration, paymentCongratsModelMapper)
    }

    @Test
    fun onPostPaymentDeepLinkUrlNotNullOrEmptyAndPaymentResultApprovedThenCreateCongratsPostPaymentResult() {
        val deepLink = "mercadopago://px/post-payment_url"

        val paymentModel = mock<PaymentModel> {
            on { paymentResult }.thenReturn(mock())
            on { paymentResult.isApproved }.thenReturn(true)
        }

        whenever(postPaymentConfiguration.getPostPaymentDeepLinkUrl()).thenReturn(deepLink)

        val congratsResult = congratsResultFactory.create(paymentModel, null)
        val expectedCongratsResult = CongratsResult.CongratsPostPaymentResult(paymentModel, deepLink)

        congratsResult.assertEquals(expectedCongratsResult)
    }

    @Test
    fun onRedirectUrlNotNullOrEmptyThenCreateSkipCongratsResult() {
        val redirectUrl = "redirect_url"

        val paymentModel = mock<PaymentModel>()

        val congratsResult = congratsResultFactory.create(paymentModel, redirectUrl)
        val expectedCongratsResult = CongratsResult.SkipCongratsResult(paymentModel)

        congratsResult.assertEquals(expectedCongratsResult)
    }

    @Test
    fun onPaymentBusinessModelThenCreateCongratsBusinessPaymentResult() {
        val businessPaymentModel = mock<BusinessPaymentModel>()
        val paymentCongratsModel = mock<PaymentCongratsModel>()

        whenever(paymentCongratsModelMapper.map(any<BusinessPaymentModel>())).thenReturn(paymentCongratsModel)

        val congratsResult = congratsResultFactory.create(businessPaymentModel, null)
        val expectedCongratsResult = CongratsResult.CongratsBusinessPaymentResult(paymentCongratsModel)

        congratsResult.assertEquals(expectedCongratsResult)
    }

    @Test
    fun onDefaultPaymentModelThenCreateCongratsPaymentResult() {
        val paymentModel = mock<PaymentModel>()

        whenever(postPaymentConfiguration.getPostPaymentDeepLinkUrl()).thenReturn(null)

        val congratsResult = congratsResultFactory.create(paymentModel, null)
        val expectedCongratsResult = CongratsResult.CongratsPaymentResult(paymentModel)

        congratsResult.assertEquals(expectedCongratsResult)
    }
}
