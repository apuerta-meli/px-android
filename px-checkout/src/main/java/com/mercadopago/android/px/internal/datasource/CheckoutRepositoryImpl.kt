package com.mercadopago.android.px.internal.datasource

import com.mercadopago.android.px.internal.adapters.NetworkApi
import com.mercadopago.android.px.internal.callbacks.ApiResponse
import com.mercadopago.android.px.internal.mappers.CustomChargeToPaymentTypeChargeMapper
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.mappers.InitRequestBodyMapper
import com.mercadopago.android.px.internal.mappers.OneTapItemToDisabledPaymentMethodMapper
import com.mercadopago.android.px.internal.repository.*
import com.mercadopago.android.px.internal.services.CheckoutService
import com.mercadopago.android.px.internal.util.ApiUtil
import com.mercadopago.android.px.model.exceptions.ApiException
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.tracking.internal.MPTracker
import kotlinx.coroutines.delay

internal typealias ApiResponseCallback<R> = ApiResponse<R, ApiException>
internal typealias ResponseCallback<R> = Response<R, MercadoPagoError>

internal open class CheckoutRepositoryImpl(
    private val paymentSettingRepository: PaymentSettingRepository,
    private val experimentsRepository: ExperimentsRepository,
    private val disabledPaymentMethodRepository: DisabledPaymentMethodRepository,
    private val networkApi: NetworkApi,
    private val tracker: MPTracker,
    private val payerPaymentMethodRepository: PayerPaymentMethodRepository,
    private val oneTapItemRepository: OneTapItemRepository,
    private val paymentMethodRepository: PaymentMethodRepository,
    private val modalRepository: ModalRepository,
    private val payerComplianceRepository: PayerComplianceRepository,
    private val amountConfigurationRepository: AmountConfigurationRepository,
    private val discountRepository: DiscountRepository,
    private val chargesRepository: ChargeRepository,
    private val customChargeToPaymentTypeChargeMapper: CustomChargeToPaymentTypeChargeMapper,
    private val initRequestBodyMapper: InitRequestBodyMapper,
    private val oneTapItemToDisabledPaymentMethodMapper: OneTapItemToDisabledPaymentMethodMapper
) : CheckoutRepository {

    override suspend fun checkout() = doCheckout(null)

    override fun configure(checkoutResponse: CheckoutResponse) {
        if (checkoutResponse.preference != null) {
            paymentSettingRepository.configure(checkoutResponse.preference)
        }
        paymentSettingRepository.configure(checkoutResponse.site)
        paymentSettingRepository.configure(checkoutResponse.currency)
        paymentSettingRepository.configure(checkoutResponse.configuration)

        // TODO: Remove null check when backend has IDC ready
        checkoutResponse.customCharges?.let {
            chargesRepository.configure(
                customChargeToPaymentTypeChargeMapper.map(it)
            )
        }

        experimentsRepository.configure(checkoutResponse.experiments)
        payerPaymentMethodRepository.configure(checkoutResponse.payerPaymentMethods)
        oneTapItemRepository.configure(checkoutResponse.oneTapItems)
        paymentMethodRepository.configure(checkoutResponse.availablePaymentMethods)
        modalRepository.configure(checkoutResponse.modals)
        payerComplianceRepository.configure(checkoutResponse.payerCompliance)
        amountConfigurationRepository.configure(checkoutResponse.defaultAmountConfiguration)
        discountRepository.configure(checkoutResponse.discountsConfigurations)
        disabledPaymentMethodRepository.configure(
            oneTapItemToDisabledPaymentMethodMapper.map(checkoutResponse.oneTapItems)
        )
        tracker.setExperiments(experimentsRepository.experiments)
    }

    override suspend fun checkoutWithNewCard(cardId: String): Response<CheckoutResponse, MercadoPagoError> {
        var retriesAvailable = MAX_REFRESH_RETRIES
        var findCardResult = checkoutAndFindCard(cardId)
        var lastSuccessResponse = findCardResult.response.takeIf { it is Response.Success }
        while (findCardResult.retryNeeded && retriesAvailable > 0) {
            retriesAvailable--
            delay(RETRY_DELAY)
            findCardResult = checkoutAndFindCard(cardId)
            if (findCardResult.response is Response.Success) {
                lastSuccessResponse = findCardResult.response
            }
        }

        return lastSuccessResponse ?: findCardResult.response
    }

    private suspend fun doCheckout(cardId: String?): ResponseCallback<CheckoutResponse> {
        val body = initRequestBodyMapper.map(paymentSettingRepository, cardId)
        val preferenceId = paymentSettingRepository.checkoutPreferenceId
        val apiResponse = networkApi.apiCallForResponse(CheckoutService::class.java) {
            if (preferenceId != null) {
                it.checkout(preferenceId, body)
            } else {
                it.checkout(body)
            }
        }
        return when (apiResponse) {
            is ApiResponse.Failure -> Response.Failure(
                MercadoPagoError(
                    apiResponse.exception,
                    ApiUtil.RequestOrigin.POST_INIT
                )
            )
            is ApiResponse.Success -> Response.Success(apiResponse.result)
        }
    }

    private suspend fun checkoutAndFindCard(cardId: String): FindCardResult {
        return when (val response = doCheckout(cardId)) {
            is Response.Success -> findCard(response, cardId)
            is Response.Failure -> FindCardResult(false, response)
        }
    }

    private fun findCard(response: Response.Success<CheckoutResponse>, cardId: String): FindCardResult {
        var retryNeeded = false
        for (node in response.result.oneTapItems) {
            if (node.isCard && node.card.id == cardId) {
                retryNeeded = node.card.retry.isNeeded
                break
            }
        }
        return FindCardResult(retryNeeded, response)
    }

    private data class FindCardResult(
        val retryNeeded: Boolean,
        var response: ResponseCallback<CheckoutResponse>
    )

    companion object {
        private const val MAX_REFRESH_RETRIES = 3
        private const val RETRY_DELAY = 5000L
    }
}
