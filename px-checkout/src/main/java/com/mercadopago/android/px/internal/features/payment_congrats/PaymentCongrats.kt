package com.mercadopago.android.px.internal.features.payment_congrats

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import com.mercadopago.android.px.internal.di.Session
import com.mercadopago.android.px.internal.features.business_result.BusinessPaymentResultActivity
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsModel
import com.mercadopago.android.px.model.IPaymentDescriptor

object PaymentCongrats {
    private const val PAYMENT_CONGRATS = "payment_congrats"
    const val PAYMENT_DESCRIPTOR = "payment_descriptor"

    /**
     * Allows to execute a congrats activity
     *
     * @param paymentCongratsModel model with the needed data to show a PaymentCongrats
     * @param activity caller activity
     * @param requestCode requestCode for ActivityForResult
     */
    @JvmStatic
    fun show(paymentCongratsModel: PaymentCongratsModel, activity: Activity, requestCode: Int) {
        Session.getInstance().init(paymentCongratsModel)
        Intent(activity, BusinessPaymentResultActivity::class.java).also {
            it.putExtra(PAYMENT_CONGRATS, paymentCongratsModel)
            activity.startActivityForResult(it, requestCode)
        }
    }

    internal fun show(paymentCongratsModel: PaymentCongratsModel, fragment: Fragment, requestCode: Int) {
        Intent(fragment.context, BusinessPaymentResultActivity::class.java).also {
            it.putExtra(PAYMENT_CONGRATS, paymentCongratsModel)
            fragment.startActivityForResult(it, requestCode)
        }
    }

    internal fun show(iPaymentDescriptor: IPaymentDescriptor?, activity: Activity?) {
        Intent(activity, CongratsActivity::class.java).also { intent ->
            iPaymentDescriptor?.let {
                intent.putExtra(PAYMENT_DESCRIPTOR, it)
            }
            activity?.startActivity(intent)
        }
    }
}