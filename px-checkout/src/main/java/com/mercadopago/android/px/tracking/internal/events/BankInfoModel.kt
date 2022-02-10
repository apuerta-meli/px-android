package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel

internal class BankInfoModel(
    customOptionId: String?,
    payerPaymentMethodRepository: PayerPaymentMethodRepository
) : TrackingMapModel() {

    private var bankName: String? = null
    private var externalAccountId: String? = null

    init {
        bankName = customOptionId?.let { payerPaymentMethodRepository[it] }?.bankInfo?.name
        externalAccountId = customOptionId?.let { payerPaymentMethodRepository[it] }?.id
    }
}
