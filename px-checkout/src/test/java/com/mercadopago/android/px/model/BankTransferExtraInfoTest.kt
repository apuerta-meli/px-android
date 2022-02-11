package com.mercadopago.android.px.model

import com.mercadopago.android.px.internal.datasource.PayerPaymentMethodRepositoryImpl
import com.mercadopago.android.px.tracking.internal.events.BankTransferExtraInfo
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockkClass
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test

private const val CUSTOM_OPTION_ID = "123456"
private const val BANK_NAME = "123456"
private const val ACCOUNT_ID = "123"

class BankTransferExtraInfoTest {

    @MockK
    private lateinit var payerPaymentMethodRepository: PayerPaymentMethodRepositoryImpl

    private lateinit var bankTransferExtraInfo: BankTransferExtraInfo

    @Before
    fun `set up`() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `when custom option id is null then returns bank name and external account id nulls`() {
        bankTransferExtraInfo = BankTransferExtraInfo(null, payerPaymentMethodRepository)

        assertNull(bankTransferExtraInfo.bankName)
        assertNull(bankTransferExtraInfo.externalAccountId)
    }

    @Test
    fun `when custom option id is not null then returns bank name and external account id`() {
        val bankTransferInfo = mockkClass(BankTransferInfo::class) {
            every { name } returns BANK_NAME
        }
        val payerPaymentMethod = mockkClass(CustomSearchItem::class) {
            every { bankInfo } returns bankTransferInfo
            every { id } returns ACCOUNT_ID
        }

        every { payerPaymentMethodRepository[CUSTOM_OPTION_ID] } returns payerPaymentMethod

        bankTransferExtraInfo = BankTransferExtraInfo(CUSTOM_OPTION_ID, payerPaymentMethodRepository)

        assertEquals(BANK_NAME, bankTransferExtraInfo.bankName)
        assertEquals(ACCOUNT_ID, bankTransferExtraInfo.externalAccountId)
        assertNotNull(bankTransferExtraInfo.bankName)
        assertNotNull(bankTransferExtraInfo.externalAccountId)
    }
}
