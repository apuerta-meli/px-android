package com.mercadopago.android.px.internal.features.payment_congrats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.di.Session
import com.mercadopago.android.px.internal.features.dummy_result.DummyResultActivity
import com.mercadopago.android.px.internal.features.payment_result.PaymentResultActivity
import com.mercadopago.android.px.model.IPaymentDescriptor

private const val REQ_CODE_CONGRATS = 300

class CongratsActivity : AppCompatActivity() {

    private val congratsViewModel: CongratsViewModel by lazy {
        Session.getInstance().viewModelModule.get(this, CongratsViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congrats)

        val iPaymentDescriptor = intent.getSerializableExtra(PaymentCongrats.PAYMENT_DESCRIPTOR) as? IPaymentDescriptor
        iPaymentDescriptor?.let {
            congratsViewModel.createCongratsResult(it)
        }

        congratsViewModel.congratsResultLiveData.observe(this, Observer { state -> state?.let { onCongratsResult(it) } })
    }

    private fun onCongratsResult(congratsResult: CongratsResult) {
        when (congratsResult) {
            is CongratsResult.CongratsPaymentResult ->
                PaymentResultActivity.start(this, REQ_CODE_CONGRATS, congratsResult.paymentModel)
            is CongratsResult.CongratsBusinessPaymentResult -> PaymentCongrats.show(
                congratsResult.paymentCongratsModel,
                this,
                REQ_CODE_CONGRATS
            )
            is CongratsResult.SkipCongratsResult ->
                DummyResultActivity.start(this, REQ_CODE_CONGRATS, congratsResult.paymentModel)
        }
        finish()
    }

    override fun onBackPressed() {
        if (congratsViewModel.congratsResultLiveData.value != null) {
            super.onBackPressed()
        }
    }

}