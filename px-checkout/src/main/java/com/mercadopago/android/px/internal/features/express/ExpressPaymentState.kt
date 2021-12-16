package com.mercadopago.android.px.internal.features.express

import com.mercadopago.android.px.internal.base.BaseState
import com.mercadopago.android.px.internal.viewmodel.SplitSelectionState
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class ExpressPaymentState @JvmOverloads constructor(
    var paymentMethodIndex: Int = 0,
    var splitSelectionState: SplitSelectionState = SplitSelectionState()
) : BaseState
