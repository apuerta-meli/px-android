package com.mercadopago.android.px.internal.features.payment_congrats.model

import android.os.Parcelable
import com.mercadopago.android.px.model.internal.Text
import com.mercadopago.android.px.model.internal.TextAlignment
import kotlinx.android.parcel.Parcelize

private const val REGULAR = "regular"

@Parcelize
data class PaymentCongratsText @JvmOverloads constructor(
    val message: String = "",
    val backgroundColor: String? = null,
    val textColor: String? = null,
    val weight: String? = REGULAR,
    val alignment: TextAlignment = TextAlignment.LEFT
) : Parcelable {
    companion object {
        val EMPTY = PaymentCongratsText()

        fun from(text: Text?) = text?.let {
            PaymentCongratsText(
                it.message,
                it.backgroundColor,
                it.textColor,
                it.weight ?: REGULAR,
                it.alignment ?: TextAlignment.LEFT
            )
        }
    }
}
