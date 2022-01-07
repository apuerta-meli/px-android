package com.mercadopago.android.px.internal.features.payment_congrats

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.mercadolibre.android.ui.widgets.MeliSpinner
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.di.viewModel
import com.mercadopago.android.px.internal.features.payment_result.PaymentResultActivity
import com.mercadopago.android.px.internal.util.ErrorUtil
import com.mercadopago.android.px.model.IPaymentDescriptor
import com.mercadopago.android.px.model.exceptions.MercadoPagoError

private const val REQ_CODE_CONGRATS = 300

internal class CongratsDeepLinkActivity : AppCompatActivity() {

    private val congratsViewModel by viewModel<CongratsViewModel>()

    private var iPaymentDescriptor: IPaymentDescriptor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congrats_deep_link)

        iPaymentDescriptor = intent.getSerializableExtra(PaymentCongrats.PAYMENT_DESCRIPTOR) as? IPaymentDescriptor
        congratsViewModel.createCongratsResult(iPaymentDescriptor)

        congratsViewModel.congratsResultLiveData.observe(
            this,
            Observer { state -> state?.let { onCongratsResult(it) } })
    }

    private fun onCongratsResult(congratsResult: CongratsResult) {
        when (congratsResult) {
            is BaseCongratsResult.PaymentResult -> {
                PaymentResultActivity.start(this, REQ_CODE_CONGRATS, congratsResult.paymentModel)
                finish()
            }
            is BaseCongratsResult.BusinessPaymentResult -> {
                PaymentCongrats.show(
                    congratsResult.paymentCongratsModel,
                    this,
                    REQ_CODE_CONGRATS
                )
                finish()
            }
            is CongratsPostPaymentResult.Loading -> if (congratsResult.isLoading) {
                findViewById<MeliSpinner>(R.id.loading_view).visibility = View.VISIBLE
            } else {
                findViewById<MeliSpinner>(R.id.loading_view).visibility = View.GONE
            }
            is CongratsPostPaymentResult.ConnectionError -> handleError(
                message = getString(R.string.px_no_connection_message),
                recoverable = true
            )
            is CongratsPostPaymentResult.BusinessError -> handleError(recoverable = false)
        }
    }

    private fun handleError(message: String = "", recoverable: Boolean) {
        ErrorUtil.startErrorActivity(this, MercadoPagoError(message, recoverable))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ErrorUtil.ERROR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                congratsViewModel.createCongratsResult(iPaymentDescriptor)
            } else {
                finish()
            }
        }
    }

    override fun onBackPressed() {
        if (congratsViewModel.congratsResultLiveData.value != null) {
            super.onBackPressed()
        }
    }
}
