package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import mu.KotlinLogging
import java.lang.Thread.sleep

class BillingService(private val paymentProvider: PaymentProvider, private val invoiceService: InvoiceService) {
    private val maxTries = 3
    private val logger = KotlinLogging.logger {}

    fun chargePendingInvoices() {
        invoiceService.fetchAllPendingInvoices().forEach { invoice -> chargeInvoice(invoice) }
    }

    private fun chargeInvoice(invoice: Invoice, tries: Int = maxTries) {
        var chargeResponse = false
        try {
            chargeResponse = paymentProvider.charge(invoice)
        } catch (e: Exception) {
            when (e) {
                is CustomerNotFoundException, is CurrencyMismatchException -> {
                    logger.error("Unable to charge invoice due to: ", e)
                    // Report this, maybe sentry or something
                }
                is NetworkException -> {
                    // you can add monitoring metrics here
                    if (tries > 0) {
                        sleep(5000) // to retry in 5 seconds
                        return chargeInvoice(invoice, tries - 1)
                    }
                    logger.error("Unable to charge invoice due to: ", e)
                }
                else -> logger.error("Unknown error occurred while charging: ", e) // we should also alert this case
            }
        }
        if (chargeResponse) invoiceService.updateStatus(InvoiceStatus.PAID, invoice)
    }
}
