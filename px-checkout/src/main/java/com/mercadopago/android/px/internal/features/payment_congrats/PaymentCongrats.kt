package com.mercadopago.android.px.internal.features.payment_congrats

import android.app.Activity
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

    /**
     * Allows to execute a congrats activity
     *
     * @param iPaymentDescriptor model with the needed data to show a Congrats
     * @param activity caller activity
     */
    @JvmStatic
    fun launchCongratsWithPayment(iPaymentDescriptor: IPaymentDescriptor, activity: Activity) {
        // SEMOVI: lanzar con deeplink
        val intent = Intent(activity, CongratsDeepLinkActivity::class.java).apply {
            putExtra(PAYMENT_DESCRIPTOR, iPaymentDescriptor)
        }
        activity.startActivity(intent)
        activity.finish()
    }
}