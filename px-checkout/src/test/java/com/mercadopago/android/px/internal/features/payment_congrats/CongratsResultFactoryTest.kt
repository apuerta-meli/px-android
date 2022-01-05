package com.mercadopago.android.px.internal.features.payment_congrats

import com.mercadopago.android.px.configuration.PostPaymentConfiguration
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModelMapper
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

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
    //semovi: Finish these tests
/*
    @Test
    fun onPostPaymentDeepLinkUrlNotNullOrEmptyAndPaymentResultApprovedThenCreateCongratsPostPaymentResult() {
        val deepLink = "mercadopago://px/post-payment_url"

        val paymentModel = mock<PaymentModel> {
            on { paymentResult }.thenReturn(mock())
            on { paymentResult.isApproved }.thenReturn(true)
        }

        whenever(postPaymentConfiguration.getPostPaymentDeepLinkUrl()).thenReturn(deepLink)

        val congratsResult = congratsResultFactory.create(paymentModel, null)
        val expectedCongratsResult = BaseCongratsResult.PostPaymentResult(paymentModel, deepLink)

        congratsResult.assertEquals(expectedCongratsResult)
    }

    @Test
    fun onRedirectUrlNotNullOrEmptyThenCreateSkipCongratsResult() {
        val redirectUrl = "redirect_url"

        val paymentModel = mock<PaymentModel>()

        val congratsResult = congratsResultFactory.create(paymentModel, redirectUrl)
        val expectedCongratsResult = CongratsPaymentResult.SkipCongratsResult(paymentModel)

        congratsResult.assertEquals(expectedCongratsResult)
    }

    @Test
    fun onPaymentBusinessModelThenCreateCongratsBusinessPaymentResult() {
        val businessPaymentModel = mock<BusinessPaymentModel>()
        val paymentCongratsModel = mock<PaymentCongratsModel>()

        whenever(paymentCongratsModelMapper.map(any<BusinessPaymentModel>())).thenReturn(paymentCongratsModel)

        val congratsResult = congratsResultFactory.create(businessPaymentModel, null)
        val expectedCongratsResult = BaseCongratsResult.CongratsBusinessPaymentResult(paymentCongratsModel)

        congratsResult.assertEquals(expectedCongratsResult)
    }

    @Test
    fun onDefaultPaymentModelThenCreateCongratsPaymentResult() {
        val paymentModel = mock<PaymentModel>()

        whenever(postPaymentConfiguration.getPostPaymentDeepLinkUrl()).thenReturn(null)

        val congratsResult = congratsResultFactory.create(paymentModel, null)
        val expectedCongratsResult = BaseCongratsResult.CongratsPaymentResult(paymentModel)

        congratsResult.assertEquals(expectedCongratsResult)
    }*/
}
