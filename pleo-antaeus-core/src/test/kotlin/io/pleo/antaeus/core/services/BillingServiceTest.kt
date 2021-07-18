package io.pleo.antaeus.core.services

import io.mockk.*
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import org.junit.jupiter.api.Test


class BillingServiceTest {
    private val pendingInvoice: Invoice =  createPendingInvoice()

    private val paymentProvider = mockk<PaymentProvider> {}

    private val invoiceService = mockk<InvoiceService> {
        every { fetchAllPendingInvoices() } returns listOf(pendingInvoice)
    }

    private val billingService = BillingService(paymentProvider = paymentProvider, invoiceService = invoiceService)

    @Test
    fun `chargePendingInvoices when the payment provider returns true for charge`() {
        every { paymentProvider.charge(pendingInvoice) } returns true
        every{ invoiceService.updateStatus(InvoiceStatus.PAID, pendingInvoice) } returns pendingInvoice.id

        billingService.chargePendingInvoices()
    }

    @Test
    fun `chargePendingInvoices when the payment provider returns false for charge`() {
        every { paymentProvider.charge(pendingInvoice) } returns false

        billingService.chargePendingInvoices()

        verify(inverse = true) { invoiceService.updateStatus(InvoiceStatus.PAID, pendingInvoice) }
    }
}