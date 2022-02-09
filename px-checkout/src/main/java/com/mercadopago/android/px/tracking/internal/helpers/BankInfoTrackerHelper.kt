package com.mercadopago.android.px.tracking.internal.helpers

import com.mercadopago.android.px.internal.repository.PayerPaymentMethodRepository
import com.mercadopago.android.px.internal.repository.UserSelectionRepository

internal class BankInfoTrackerHelper(
    val payerPaymentMethodRepository: PayerPaymentMethodRepository,
    val userSelectionRepository: UserSelectionRepository
) {

    fun getBankName(): String? {
        return userSelectionRepository.customOptionId?.let { payerPaymentMethodRepository[it] }?.bankInfo?.name
    }

    fun getExternalAccountId(): String? {
        return userSelectionRepository.customOptionId?.let { payerPaymentMethodRepository[it] }?.id
    }
}
