package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel

internal class BankTransferExtraInfo(
    customOptionId: String?,
    payerPaymentMethodRepository: PayerPaymentMethodRepository
) : TrackingMapModel() {

    var bankName: String? = null
    var externalAccountId: String? = null

    init {
        bankName = customOptionId?.let { payerPaymentMethodRepository[it] }?.bankInfo?.name
        externalAccountId = customOptionId?.let { payerPaymentMethodRepository[it] }?.id
    }
}
