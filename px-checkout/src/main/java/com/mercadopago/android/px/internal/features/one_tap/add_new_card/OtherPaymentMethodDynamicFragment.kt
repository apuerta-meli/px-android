package com.mercadopago.android.px.internal.features.one_tap.add_new_card

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.viewmodel.drawables.OtherPaymentMethodFragmentItem

class OtherPaymentMethodDynamicFragment : OtherPaymentMethodFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val smallMode = model.newCardMetadata != null && model.offlineMethodsMetadata != null
        return inflater.inflate(if (smallMode) R.layout.px_fragment_other_payment_method_small_dynamic else
            R.layout.px_fragment_other_payment_method_large_dynamic, container, false)
    }

    companion object {
        fun getInstance(model: OtherPaymentMethodFragmentItem) = OtherPaymentMethodDynamicFragment().also {
            it.storeModel(model)
        }
    }
}