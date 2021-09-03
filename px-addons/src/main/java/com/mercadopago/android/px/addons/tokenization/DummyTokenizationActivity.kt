package com.mercadopago.android.px.addons.tokenization

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

internal class DummyTokenizationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setResult(
            RESULT_OK,
            Intent().putExtra(TOKENIZATION_RESPONSE, TokenizationResponse(TokenizationResponse.State.SUCCESS))
        )
        finish()
    }

    companion object {
        fun start(fragment: Fragment, requestCode: Int) {
            Intent(fragment.requireContext(), DummyTokenizationActivity::class.java).also {
                fragment.startActivityForResult(it, requestCode)
            }
        }

        fun start(activity: Activity, requestCode: Int) {
            Intent(activity, DummyTokenizationActivity::class.java).also {
                activity.startActivityForResult(it, requestCode)
            }
        }
    }
}
