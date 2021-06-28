package com.mercadopago.android.px.internal.usecases

import com.mercadopago.android.px.CallbackTest
import com.mercadopago.android.px.TestContextProvider
import com.mercadopago.android.px.internal.callbacks.ApiResponse
import com.mercadopago.android.px.internal.datasource.CheckoutRepositoryImpl
import com.mercadopago.android.px.internal.domain.CheckoutUseCase
import com.mercadopago.android.px.internal.repository.CheckoutRepository
import com.mercadopago.android.px.mocks.CheckoutResponseStub
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.tracking.internal.MPTracker
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.internal.matchers.apachecommons.ReflectionEquals
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.*

@RunWith(MockitoJUnitRunner::class)
class CheckoutUseCaseTest {

    @Mock
    private lateinit var success: CallbackTest<CheckoutResponse>

    @Mock
    private lateinit var failure: CallbackTest<MercadoPagoError>

    @Mock
    private lateinit var tracker: MPTracker

    @Mock
    private lateinit var checkoutRepository: CheckoutRepository

    private lateinit var testContextProvider: TestContextProvider
    private lateinit var checkoutUseCase: CheckoutUseCase

    @Before
    fun setUp() {
        testContextProvider = TestContextProvider()
        checkoutUseCase = CheckoutUseCase(checkoutRepository, tracker, testContextProvider)
    }

    @Test
    fun whenApiReturnsSuccessAndNoCardIdProvidedThenItShouldReturnSuccessWithResponse() {
        val checkoutResponse = CheckoutResponseStub.FULL.get()
        runBlocking {
            whenever(checkoutRepository.checkout()).thenReturn(ApiResponse.Success(checkoutResponse))
        }
        checkoutUseCase.execute(
            CheckoutUseCase.CheckoutParams(null),
            success::invoke,
            failure::invoke
        )
        verify(success).invoke(argThat { ReflectionEquals(checkoutResponse).matches(this) })
    }

    @Test
    fun whenApiReturnsSuccessAndNoCardIdProvidedThenItShouldCallCheckoutAndConfigureButNotSort() {
        val checkoutResponse = CheckoutResponseStub.FULL.get()
        runBlocking {
            whenever(checkoutRepository.checkout()).thenReturn(ApiResponse.Success(checkoutResponse))
        }
        checkoutUseCase.execute(
            CheckoutUseCase.CheckoutParams(null),
            success::invoke,
            failure::invoke
        )
        runBlocking {
            verify(checkoutRepository).checkout()
        }
        verify(checkoutRepository).configure(checkoutResponse)
        verifyNoMoreInteractions(checkoutRepository)
    }

    @Test
    fun whenApiReturnsSuccessAndCardIdProvidedThenItShouldCallSort() {
        val checkoutResponse = CheckoutResponseStub.ONE_TAP_VISA_CREDIT_CARD.get()
        val oneTapItem = checkoutResponse.oneTapItems.first { it.isCard }
        runBlocking {
            whenever(checkoutRepository.checkout()).thenReturn(ApiResponse.Success(checkoutResponse))
        }
        checkoutUseCase.execute(
            CheckoutUseCase.CheckoutParams(oneTapItem.card.id),
            success::invoke,
            failure::invoke
        )
        runBlocking {
            verify(checkoutRepository).checkout()
        }
        verify(checkoutRepository).sortByPrioritizedCardId(any(), argThat { this == oneTapItem.card.id })
    }

    @Test
    fun whenApiResponseDoesNotHaveCardThenItShouldRetryAndReturnRecoverableMPError() {
        runBlocking {
            // This is needed because if we don't use the same context on runBlocking and on UseCase
            // then when we use delay the test fails.
            val newCheckoutUseCase = CheckoutUseCase(
                checkoutRepository, tracker,
                TestContextProvider(coroutineContext, coroutineContext)
            )
            whenever(checkoutRepository.checkout()).thenReturn(
                ApiResponse.Success(CheckoutResponseStub.ONE_TAP_VISA_CREDIT_CARD.get())
            )
            newCheckoutUseCase.execute(
                CheckoutUseCase.CheckoutParams("123"),
                success::invoke,
                failure::invoke
            )
        }
        runBlocking {
            verify(checkoutRepository, atLeast(2)).checkout()
        }
        verifyNoMoreInteractions(checkoutRepository)
        verify(failure).invoke(
            argThat { this.apiException.message.contains("Card not found") && this.isRecoverable }
        )
    }

    @Test
    fun whenApiResponseHaveCardWithRetryAndOnSecondCallNoRetryNeededThenItShouldRetryAndReturnSuccess() {
        val checkoutResponse = CheckoutResponseStub.ONE_TAP_VISA_CREDIT_CARD.get()
        val retryCheckoutResponse = CheckoutResponseStub.ONE_TAP_CREDIT_CARD_WITH_RETRY.get()
        val cardFoundWithRetryId = checkoutResponse.oneTapItems.first().card.id

        runBlocking {
            // This is needed because if we don't use the same context on runBlocking and on UseCase
            // then when we use delay the test fails.
            val newCheckoutUseCase = CheckoutUseCase(
                checkoutRepository, tracker,
                TestContextProvider(coroutineContext, coroutineContext)
            )
            whenever(checkoutRepository.checkout()).thenReturn(
                ApiResponse.Success(retryCheckoutResponse),
                ApiResponse.Success(checkoutResponse)
            )
            newCheckoutUseCase.execute(
                CheckoutUseCase.CheckoutParams(cardFoundWithRetryId),
                success::invoke,
                failure::invoke
            )
        }
        runBlocking {
            verify(checkoutRepository, times(2)).checkout()
        }
        verify(checkoutRepository).sortByPrioritizedCardId(any(), any())
        verify(checkoutRepository).configure(checkoutResponse)
        verify(success).invoke(argThat { ReflectionEquals(checkoutResponse).matches(this) })
    }

    @Test
    fun whenApiReturnsFailureThenItShouldReturnRecoverableMercadoPagoErrorWithApiException() {
        val apiExceptionMsg = "test message"
        val apiException = ApiException().apply { message = apiExceptionMsg }
        runBlocking {
            whenever(checkoutRepository.checkout()).thenReturn(ApiResponse.Failure(apiException))
        }
        checkoutUseCase.execute(
            CheckoutUseCase.CheckoutParams(null),
            success::invoke,
            failure::invoke
        )
        runBlocking {
            verify(checkoutRepository).checkout()
            verifyNoMoreInteractions(checkoutRepository)
        }
        verifyZeroInteractions(success)
        verify(failure).invoke(argThat { this.apiException.message == apiExceptionMsg && this.isRecoverable })
    }

    @Test(expected = Exception::class)
    fun whenRepositoryThrowsExceptionThenItShouldReturnNoRecoverableMercadoPagoError() {
        val exceptionMsg = "test message"
        val exception = Exception().apply {
            whenever(localizedMessage).thenReturn(exceptionMsg)
        }
        runBlocking {
            whenever(checkoutRepository.checkout()).thenThrow(exception)
        }
        checkoutUseCase.execute(
            CheckoutUseCase.CheckoutParams(null),
            success::invoke,
            failure::invoke
        )
        runBlocking {
            verify(checkoutRepository).checkout()
            verifyNoMoreInteractions(checkoutRepository)
        }
        verifyZeroInteractions(success)
        verify(failure).invoke(argThat { this.apiException.message == exceptionMsg && !this.isRecoverable })
    }


    @Test
    fun whenApiResponseHaveCardWithRetryAndOnSubsequentCallsApiFailsItShouldReturnRecoverableMPError() {
        val retryCheckoutResponse = CheckoutResponseStub.ONE_TAP_CREDIT_CARD_WITH_RETRY.get()
        val cardFoundWithRetryId = retryCheckoutResponse.oneTapItems.first().card.id
        val exMsg = "Test exception msg"

        runBlocking {
            // This is needed because if we don't use the same context on runBlocking and on UseCase
            // then when we use delay the test fails.
            val newCheckoutUseCase = CheckoutUseCase(
                checkoutRepository, tracker,
                TestContextProvider(coroutineContext, coroutineContext)
            )
            whenever(checkoutRepository.checkout()).thenReturn(
                ApiResponse.Success(retryCheckoutResponse),
                ApiResponse.Failure(ApiException().apply { message = exMsg })
            )
            newCheckoutUseCase.execute(
                CheckoutUseCase.CheckoutParams(cardFoundWithRetryId),
                success::invoke,
                failure::invoke
            )
        }
        runBlocking {
            verify(checkoutRepository, atLeast(2)).checkout()
        }
        verify(failure).invoke(argThat { this.apiException.message == exMsg && this.isRecoverable })
    }
}