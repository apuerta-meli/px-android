package com.mercadopago.android.px.internal.features.payment_result

import com.mercadopago.android.px.configuration.AdvancedConfiguration
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import com.mercadopago.android.px.model.PaymentResult
import com.mercadopago.android.px.tracking.internal.MPTracker
import com.mercadopago.android.px.tracking.internal.views.ResultViewTrack
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
class PaymentResultPresenterTest {

    @Mock
    private lateinit var tracker: MPTracker

    private lateinit var presenter: PaymentResultPresenter

    @Before
    fun setUp() {
        val paymentsSettings = mock<PaymentSettingRepository>()
        val advancedConfiguration = mock<AdvancedConfiguration>()
        val paymentModel = mock<PaymentModel>()
        val paymentResult = mock<PaymentResult>()
        whenever(paymentModel.paymentResult).thenReturn(paymentResult)
        whenever(paymentModel.congratsResponse).thenReturn(mock())
        whenever(paymentModel.remedies).thenReturn(mock())
        whenever(paymentResult.paymentData).thenReturn(mock())
        whenever(paymentsSettings.checkoutPreference).thenReturn(mock())
        whenever(paymentsSettings.currency).thenReturn(mock())
        whenever(paymentsSettings.advancedConfiguration).thenReturn(advancedConfiguration)
        whenever(advancedConfiguration.paymentResultScreenConfiguration).thenReturn(mock())
        presenter = PaymentResultPresenter(
            paymentsSettings,
            paymentModel,
            mock(),
            true,
            mock(),
            mock(),
            tracker
        )
    }

    @Test
    fun whenOnFreshStartThenTrackView() {
        presenter.onFreshStart()

        verify(tracker).track(any<ResultViewTrack>())
        verifyNoMoreInteractions(tracker)
    }

    @Test
    fun whenOnDirtyStartThenNotTrackView() {
        verifyNoMoreInteractions(tracker)
    }
}
