package com.mercadopago.android.px.model.internal

import java.math.BigDecimal

data class PaymentTypeChargeRuleDM (
    val paymentTypeId: String,
    val charge: BigDecimal,
    val message: String?,
    val taxable: Boolean
)
