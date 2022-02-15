package com.mercadopago.android.px.internal.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.mercadopago.android.px.R
import com.mercadopago.android.px.core.commons.extensions.isNotNullNorEmpty
import com.mercadopago.android.px.core.presentation.extensions.loadOrElse
import com.mercadopago.android.px.core.presentation.extensions.loadOrGone
import com.mercadopago.android.px.internal.features.payment_congrats.adapter.ExtraInfoAdapter
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentCongratsText
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentInfo
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentResultExtraInfo
import com.mercadopago.android.px.internal.features.payment_congrats.model.PaymentResultInfo
import com.mercadopago.android.px.internal.util.TextUtil
import com.mercadopago.android.px.internal.util.ViewUtils
import com.mercadopago.android.px.model.PaymentTypes
import java.util.Locale

internal class PaymentResultMethod @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var icon: ImageView
    private var description: MPTextView
    private var paymentMethodStatement: MPTextView
    private var statement: MPTextView
    private var amount: PaymentResultAmount
    private var infoTitle: MPTextView
    private var infoSubtitle: MPTextView
    private var extraInfo: AdapterLinearLayout

    init {
        inflate(context, R.layout.px_payment_result_method, this)
        icon = findViewById(R.id.icon)
        description = findViewById(R.id.description)
        paymentMethodStatement = findViewById(R.id.pm_statement)
        statement = findViewById(R.id.statement)
        amount = findViewById(R.id.amount)
        infoTitle = findViewById(R.id.info_title)
        infoSubtitle = findViewById(R.id.info_subtitle)
        extraInfo = findViewById(R.id.extra_info)
    }

    fun setModel(model: Model) {
        icon.loadOrElse(model.imageUrl, R.drawable.px_generic_method)
        description.loadOrGone(getDescription(model))
        ViewUtils.loadOrGone(model.paymentMethodDescriptionText, paymentMethodStatement)
        statement.loadOrGone(getStatement(model))
        amount.setModel(model.amountModel)
        renderInfo(model.info)
        renderExtraInfo(model.extraInfo)
    }

    private fun getStatement(model: Model): String? {
        if (PaymentTypes.isCardPaymentType(model.paymentTypeId) && model.statement.isNotNullNorEmpty()) {
            return TextUtil.format(context, R.string.px_text_state_account_activity_congrats, model.statement)
        } else if (PaymentTypes.isBankTransfer(model.paymentTypeId) && model.statementText != null) {
            return model.statementText.message
        }
        return null
    }

    private fun getDescription(model: Model): String? {
        return if (PaymentTypes.isCardPaymentType(model.paymentTypeId)) {
            String.format(
                Locale.getDefault(), "%s %s %s",
                model.paymentMethodName,
                resources.getString(R.string.px_ending_in),
                model.lastFourDigits
            )
        } else if (PaymentTypes.isBankTransfer(model.paymentTypeId) && model.descriptionText != null) {
            model.descriptionText.message
        } else if (!PaymentTypes.isAccountMoney(model.paymentTypeId) || model.paymentMethodDescriptionText.message == null) {
            model.paymentMethodName
        } else {
            null
        }
    }

    private fun renderExtraInfo(info: PaymentResultExtraInfo?) {
        info?.let {
            extraInfo.isVisible = true
            extraInfo.setAdapter(ExtraInfoAdapter(context, it.details))
        }
    }

    private fun renderInfo(info: PaymentResultInfo?) {
        infoTitle.loadOrGone(info?.title)
        infoSubtitle.loadOrGone(info?.subtitle)
    }

    class Model internal constructor(builder: Builder) {
        val paymentMethodId: String = builder.paymentMethodId
        val paymentMethodName: String = builder.paymentMethodName
        val imageUrl: String? = builder.imageUrl
        val paymentMethodDescriptionText: PaymentCongratsText = builder.paymentMethodDescriptionText
        val paymentTypeId: String = builder.paymentTypeId
        val amountModel: PaymentResultAmount.Model = builder.amountModel
        val lastFourDigits: String? = builder.lastFourDigits
        val statement: String? = builder.statement
        val info: PaymentResultInfo? = builder.info
        val extraInfo: PaymentResultExtraInfo? = builder.extraInfo
        val descriptionText: PaymentCongratsText? = builder.descriptionText
        val statementText: PaymentCongratsText? = builder.statementText

        class Builder(
            var paymentMethodName: String,
            var imageUrl: String?,
            val paymentMethodDescriptionText: PaymentCongratsText,
            var paymentTypeId: String,
            var amountModel: PaymentResultAmount.Model
        ) {
            var paymentMethodId: String = ""
            var lastFourDigits: String? = null
            var statement: String? = null
            var info: PaymentResultInfo? = null
            var extraInfo: PaymentResultExtraInfo? = null
            var descriptionText: PaymentCongratsText? = null
            var statementText: PaymentCongratsText? = null

            fun setLastFourDigits(lastFourDigits: String?) = apply { this.lastFourDigits = lastFourDigits }

            fun setInfo(info: PaymentResultInfo?) = apply { this.info = info }

            fun setExtraInfo(extraInfo: PaymentResultExtraInfo?) = apply { this.extraInfo = extraInfo }

            fun setDescriptionText(descriptionText: PaymentCongratsText?) = apply { this.descriptionText = descriptionText }

            fun setStatementText(statementText: PaymentCongratsText?) = apply { this.statementText = statementText }

            fun setStatement(statement: String?) = apply { this.statement = statement }

            fun build(): Model = Model(this)
        }

        companion object {
            @JvmStatic
            fun with(paymentInfo: PaymentInfo, statement: String?): Model {
                with(paymentInfo) {
                    val amountModel = PaymentResultAmount.Model.Builder(paidAmount, rawAmount)
                        .setDiscountName(discountName)
                        .setNumberOfInstallments(installmentsCount)
                        .setInstallmentsAmount(installmentsAmount)
                        .setInstallmentsRate(installmentsRate)
                        .setInstallmentsTotalAmount(installmentsTotalAmount)
                        .build()
                    val description = paymentMethodDescriptionText ?: PaymentCongratsText.EMPTY
                    return Builder(paymentMethodName, iconUrl, description, paymentMethodType.value, amountModel)
                        .setLastFourDigits(lastFourDigits)
                        .setInfo(consumerCreditsInfo)
                        .setExtraInfo(extraInfo)
                        .setDescriptionText(descriptionText)
                        .setStatement(statement)
                        .setStatementText(statementText)
                        .build()
                }
            }
        }
    }
}
