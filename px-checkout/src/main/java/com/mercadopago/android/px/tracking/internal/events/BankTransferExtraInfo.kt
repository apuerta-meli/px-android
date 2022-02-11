package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel

internal class BankTransferExtraInfo @JvmOverloads constructor(
    customOptionId: String?,
    payerPaymentMethodRepository: PayerPaymentMethodRepository
) : TrackingMapModel() {

    private val bankName: String? = customOptionId?.let { payerPaymentMethodRepository[it] }?.bankInfo?.name
    private val externalAccountId: String? = customOptionId?.let { payerPaymentMethodRepository[it] }?.id
}
