package com.mercadopago.android.px.internal.features.payment_congrats

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mercadopago.android.px.configuration.AdvancedConfiguration
import com.mercadopago.android.px.configuration.PostPaymentConfiguration
import com.mercadopago.android.px.internal.core.ConnectionHelper
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel
import com.mercadopago.android.px.internal.repository.CongratsRepository
import com.mercadopago.android.px.internal.repository.PaymentRepository
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel
import com.mercadopago.android.px.internal.viewmodel.PaymentModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CongratsViewModelTest {

    private lateinit var congratsViewModel: CongratsViewModel

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    @Mock
    private lateinit var state: CongratsViewModel.State
    @Mock
    private lateinit var congratsRepository: CongratsRepository
    @Mock
    private lateinit var paymentRepository: PaymentRepository
    @Mock
    private lateinit var congratsResultFactory: CongratsResultFactory
    @Mock
    private lateinit var connectionHelper: ConnectionHelper
    @Mock
    private lateinit var paymentSettingRepository: PaymentSettingRepository
    @Mock
    private lateinit var congratsResultLiveData: Observer<CongratsResult>
    @Mock
    private lateinit var advancedConfiguration: AdvancedConfiguration
    @Mock
    private lateinit var postPaymentConfiguration: PostPaymentConfiguration
    @Mock
    private lateinit var paymentCongratsModel: PaymentCongratsModel


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        whenever(advancedConfiguration.postPaymentConfiguration).thenReturn(postPaymentConfiguration)
        whenever(paymentSettingRepository.advancedConfiguration).thenReturn(advancedConfiguration)

        congratsViewModel = CongratsViewModel(
            congratsRepository,
            paymentRepository,
            congratsResultFactory,
            connectionHelper,
            paymentSettingRepository,
            mock()
        )

        congratsViewModel.congratsResultLiveData.observeForever(congratsResultLiveData)
        congratsViewModel.restoreState(state)
    }

    @Test
    fun whenNonConnectionInternet() = runBlockingTest {
        whenever(connectionHelper.hasConnection()).thenReturn(false)
        congratsViewModel.createCongratsResult(mock())
        verify(congratsResultLiveData).onChanged(CongratsPostPaymentResult.Loading(true))
        verify(congratsResultLiveData).onChanged(CongratsPostPaymentResult.Loading(false))
        verify(congratsResultLiveData).onChanged(CongratsPostPaymentResult.ConnectionError)
    }

    @Test
    fun whenCreateCongratsResultHasConnectionAndIPaymentDescriptorIsNull() = runBlockingTest {
        whenever(connectionHelper.hasConnection()).thenReturn(true)
        congratsViewModel.createCongratsResult(null)
        verify(congratsResultLiveData).onChanged(CongratsPostPaymentResult.Loading(true))
        verify(congratsResultLiveData).onChanged(CongratsPostPaymentResult.Loading(false))
        verify(congratsResultLiveData).onChanged(CongratsPostPaymentResult.BusinessError)
    }

    // SEMOVI: Estos dos casos son los que quedan pendiente para terminar de cubrir toda la clase
    /*@Test
    fun whenCreateCongratsResultHasConnectionAndIPaymentDescriptorIsNotNullAndTypePaymentModel() = runBlockingTest {
        whenever(connectionHelper.hasConnection()).thenReturn(true)

        val paymentModel = mock<PaymentModel>{
            on { payment }.thenReturn(mock())
        }
        whenever(paymentModel.payment?.let { paymentRepository.createPaymentResult(it) }).thenReturn(mock())

        congratsViewModel.createCongratsResult(paymentModel.payment)
        verify(congratsResultLiveData).onChanged(CongratsPostPaymentResult.Loading(true))
        congratsViewModel.handleResult(paymentModel)
        verify(congratsResultLiveData).onChanged(CongratsPostPaymentResult.Loading(false))
        verify(congratsResultLiveData).onChanged(BaseCongratsResult.PaymentResult(paymentModel))
    }

    @Test
    fun whenCreateCongratsResultHasConnectionAndIPaymentDescriptorIsNotNullAndTypeBusinessModel() = runBlockingTest {
        whenever(connectionHelper.hasConnection()).thenReturn(true)

        val businessModel = mock<BusinessPaymentModel>{
            on { payment }.thenReturn(mock())
        }

        whenever(paymentRepository.createPaymentResult(businessModel.payment)).thenReturn(mock())

        congratsViewModel.createCongratsResult(businessModel.payment)
        verify(congratsResultLiveData).onChanged(CongratsPostPaymentResult.Loading(true))
        congratsViewModel.handleResult(businessModel)
        verify(congratsResultLiveData).onChanged(CongratsPostPaymentResult.Loading(false))
        verify(congratsResultLiveData).onChanged(BaseCongratsResult.BusinessPaymentResult(paymentCongratsModel))
    }*/

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}