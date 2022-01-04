package com.mercadopago.android.px.internal.features.payment_congrats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.mercadolibre.android.andesui.snackbar.action.AndesSnackbarAction
import com.mercadolibre.android.ui.widgets.ErrorView
import com.mercadolibre.android.ui.widgets.MeliSpinner
import com.mercadopago.android.px.R
import com.mercadopago.android.px.internal.di.Session
import com.mercadopago.android.px.internal.extensions.showSnackBar
import com.mercadopago.android.px.internal.features.payment_result.PaymentResultActivity
import com.mercadopago.android.px.model.IPaymentDescriptor


private const val REQ_CODE_CONGRATS = 300

class CongratsDeepLinkActivity : AppCompatActivity() {

    private val congratsViewModel: CongratsViewModel by lazy {
        Session.getInstance().viewModelModule.get(this, CongratsViewModel::class.java)
    }

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
            is CongratsResult.CongratsPaymentResult -> {
                PaymentResultActivity.start(this, REQ_CODE_CONGRATS, congratsResult.paymentModel)
                finish()
            }
            is CongratsResult.CongratsBusinessPaymentResult -> {
                PaymentCongrats.show(
                    congratsResult.paymentCongratsModel,
                    this,
                    REQ_CODE_CONGRATS
                )
                finish()
            }
            is CongratsResult.SkipCongratsResult -> TODO("this is never going to happen, refactor")
            is CongratsResult.Loading -> if (congratsResult.isLoading) {
                findViewById<MeliSpinner>(R.id.loading_view).visibility = View.VISIBLE
            } else {
                findViewById<MeliSpinner>(R.id.loading_view).visibility = View.GONE
            }
            is CongratsResult.ConnectionError -> resolveConnectionError(congratsResult)
            is CongratsResult.BusinessError -> {
                val action = AndesSnackbarAction(
                    getString(R.string.px_snackbar_error_action), View.OnClickListener {
                        onBackPressed()
                    })
                findViewById<View>(android.R.id.content).showSnackBar(
                    getString(R.string.px_error_title),
                    andesSnackbarAction = action
                )
            }
        }
    }

    private fun resolveConnectionError(congratsResult: CongratsResult.ConnectionError) {
        findViewById<ErrorView>(R.id.error_view).apply {
            visibility = View.VISIBLE
            setTitle(congratsResult.message)
            setButton(congratsResult.actionMessage) {
                congratsViewModel.createCongratsResult(iPaymentDescriptor)
            }
        }
    }

    override fun onBackPressed() {
        if (congratsViewModel.congratsResultLiveData.value != null) {
            super.onBackPressed()
        }
    }
}