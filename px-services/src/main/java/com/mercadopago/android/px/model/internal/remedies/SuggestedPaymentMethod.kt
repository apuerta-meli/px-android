package com.mercadopago.android.px.model.internal.remedies

import android.os.Parcelable
import com.mercadopago.android.px.model.internal.Modal
import com.mercadopago.android.px.model.internal.Text
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SuggestedPaymentMethod(
    val alternativePaymentMethod: RemedyPaymentMethod,
    val message: String,
    val title: String,
    val bottomMessage: Text?,
    val modal: Modal?
) : Parcelable