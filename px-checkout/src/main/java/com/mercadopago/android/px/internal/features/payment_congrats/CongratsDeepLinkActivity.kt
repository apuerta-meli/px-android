package com.mercadopago.android.px.internal.features.payment_congrats

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mercadolibre.android.ui.widgets.MeliSpinner
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.di.viewModel
import com.mercadopago.android.px.internal.features.payment_result.PaymentResultActivity
import com.mercadopago.android.px.internal.util.ErrorUtil
import com.mercadopago.android.px.internal.util.nonNullObserve
import com.mercadopago.android.px.model.IPaymentDescriptor
import com.mercadopago.android.px.model.exceptions.MercadoPagoError

private const val REQ_CODE_CONGRATS = 300

internal class CongratsDeepLinkActivity : AppCompatActivity() {

    private val congratsViewModel by viewModel<CongratsViewModel>()

    private var iPaymentDescriptor: IPaymentDescriptor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congrats_deep_link)

        iPaymentDescriptor = intent.getSerializableExtra(PAYMENT_DESCRIPTOR) as? IPaymentDescriptor
        congratsViewModel.createCongratsResult(iPaymentDescriptor)

        congratsViewModel.congratsResultLiveData.nonNullObserve(this) { onCongratsResult(it) }
    }

    private fun onCongratsResult(congratsResult: CongratsResult) {
        when (congratsResult) {
            is CongratsResult.PaymentResult -> {
                PaymentResultActivity.start(this, REQ_CODE_CONGRATS, congratsResult.paymentModel)
                finish()
            }
            is CongratsResult.BusinessPaymentResult -> {
                PaymentCongrats.show(congratsResult.paymentCongratsModel, this, REQ_CODE_CONGRATS)
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
        findViewById<MeliSpinner>(R.id.loading_view).visibility = View.GONE
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
        if (congratsViewModel.congratsResultLiveData.value == CongratsPostPaymentResult.Loading(false)) {
            super.onBackPressed()
        }
    }
}
