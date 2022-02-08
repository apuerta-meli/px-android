package com.mercadopago.android.px.tracking.internal.events

import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository
import com.mercadopago.android.px.tracking.internal.model.TrackingMapModel

internal class RemedyModalViewModel(
    payerPaymentMethodRepository: PayerPaymentMethodRepository,
    userSelectionRepository: UserSelectionRepository
) : TrackingMapModel() {

    private var bankName: String? = null
    private var externalAccountId: String? = null

    init {
        bankName = userSelectionRepository.customOptionId?.let { payerPaymentMethodRepository[it] }?.bankInfo?.name
        externalAccountId = userSelectionRepository.customOptionId?.let { payerPaymentMethodRepository[it] }?.id
    }
}
