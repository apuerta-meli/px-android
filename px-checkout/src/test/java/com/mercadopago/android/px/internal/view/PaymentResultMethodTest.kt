package com.mercadopago.android.px.internal.view

import android.widget.TextView
import com.mercadopago.android.px.*
import com.mercadopago.android.px.assertText
import com.mercadopago.android.px.assertVisible
import com.mercadopago.android.px.getField
import com.mercadopago.android.px.internal.util.JsonUtil
import com.mercadopago.android.px.model.*
import com.mercadopago.android.px.model.display_info.DisplayInfo
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import java.math.BigDecimal

@RunWith(RobolectricTestRunner::class)
class PaymentResultMethodTest : BasicRobolectricTest() {

    @Mock
    private lateinit var currency: Currency

    private lateinit var methodView: PaymentResultMethod

    @Before
    fun setUp() {
        methodView = PaymentResultMethod(getContext())

        `when`(currency.decimalPlaces).thenReturn(2)
        `when`(currency.decimalSeparator).thenReturn(',')
        `when`(currency.thousandsSeparator).thenReturn('.')
    }

    @Test
    fun whenInitWithCreditCardThenViewsAreCorrectlyLabeled() {
        val paymentMethodName = "Mastercard"
        val paymentMethodStatement = "pm_statement"
        val lastFourDigits = "2222"
        val discount = mock(Discount::class.java)
        `when`(discount.name).thenReturn("discount name")
        `when`(discount.couponAmount).thenReturn(BigDecimal.ONE)
        val infoTitle = "infoTitle"
        val infoSubtitle = "infoSubtitle"
        val displayInfo = JsonUtil.fromJson("""{
            "result_info": {
                "title": "$infoTitle",
                "subtitle": "$infoSubtitle"
            },
            "description": {
                "message": "$paymentMethodStatement",
                "text_color": "#ffffff",
                "background_color": "#000000",
                "weight": "regular"
            }
        }""".trimIndent(), DisplayInfo::class.java)
        val paymentMethod = mock(PaymentMethod::class.java)
        `when`(paymentMethod.paymentTypeId).thenReturn(PaymentTypes.CREDIT_CARD)
        `when`(paymentMethod.displayInfo).thenReturn(displayInfo)
        `when`(paymentMethod.name).thenReturn(paymentMethodName)
        val token = mock(Token::class.java)
        `when`(token.lastFourDigits).thenReturn(lastFourDigits)
        val paymentData = PaymentData.Builder()
            .setToken(token)
            .setDiscount(discount)
            .setRawAmount(BigDecimal.TEN)
            .setNoDiscountAmount(BigDecimal.TEN)
            .setPaymentMethod(paymentMethod)
            .createPaymentData()

        methodView.setModel(PaymentResultMethod.Model.with(null, paymentData, currency))

        with(methodView) {
            getField<TextView>("description").apply {
                assertText("$paymentMethodName completed in $lastFourDigits")
                assertVisible()
            }
            getField<TextView>("infoTitle").apply {
                assertText(infoTitle)
                assertVisible()
            }
            getField<TextView>("infoSubtitle").apply {
                assertText(infoSubtitle)
                assertVisible()
            }
            getField<TextView>("paymentMethodStatement").apply {
                assertText(paymentMethodStatement)
                assertVisible()
            }
            getField<TextView>("statement").assertGone()
        }
    }
}
