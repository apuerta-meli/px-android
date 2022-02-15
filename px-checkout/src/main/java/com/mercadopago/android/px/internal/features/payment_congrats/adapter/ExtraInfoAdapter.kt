package com.mercadopago.android.px.internal.features.payment_congrats.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsText
import com.mercadopago.android.px.internal.view.MPTextView

internal class ExtraInfoAdapter(
    context: Context,
    models: List<PaymentCongratsText>
) : ArrayAdapter<PaymentCongratsText>(context, 0, models) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup) = MPTextView(context).apply {
        getItem(position)?.also {
            setText(it)
            setLineSpacing(resources.getDimension(R.dimen.px_xxxs_margin), 1.0f)
        }
    }
}
