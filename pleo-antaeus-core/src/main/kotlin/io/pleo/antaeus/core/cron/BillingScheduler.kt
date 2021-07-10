package io.pleo.antaeus.core.cron

import io.pleo.antaeus.core.services.BillingService
import org.quartz.Job
import org.quartz.JobExecutionContext

class BillingScheduler: Job {
    override fun execute(context: JobExecutionContext) {
        val service = context.jobDetail.jobDataMap["billingService"] as BillingService
        service.chargePendingInvoices()
    }
}
