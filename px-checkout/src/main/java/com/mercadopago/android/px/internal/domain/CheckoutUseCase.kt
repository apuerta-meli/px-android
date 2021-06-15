package com.mercadopago.android.px.internal.domain

import com.mercadopago.android.px.internal.base.use_case.UseCase
import com.mercadopago.android.px.internal.callbacks.Response
import com.mercadopago.android.px.internal.repository.CheckoutRepository
import com.mercadopago.android.px.model.exceptions.MercadoPagoError
import com.mercadopago.android.px.model.internal.CheckoutResponse
import com.mercadopago.android.px.tracking.internal.MPTracker

internal class CheckoutUseCase(
    private val checkoutRepository: CheckoutRepository,
    tracker: MPTracker,
    override val contextProvider: CoroutineContextProvider
) : UseCase<String, CheckoutResponse>(tracker) {

    override suspend fun doExecute(cardId: String): Response<CheckoutResponse, MercadoPagoError> {

        val response
        if (true)
           response  = checkoutRepository.checkout()
        else
            response = checkoutRepository.refreshWithNewCard(cardId)

        return response
    }

}