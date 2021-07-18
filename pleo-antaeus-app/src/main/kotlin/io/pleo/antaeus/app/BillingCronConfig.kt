package io.pleo.antaeus.app

import io.pleo.antaeus.core.cron.BillingScheduler
import io.pleo.antaeus.core.services.BillingService
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory

class BillingCronConfig {

     fun initialize(billingService: BillingService) {
        val job1: JobDetail = JobBuilder.newJob(BillingScheduler::class.java)
                .build()
        job1.jobDataMap["billingService"] = billingService
        val trigger1: Trigger = TriggerBuilder.newTrigger()
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 9 1 1/1 ? *"))
                .build()
        val scheduler1: Scheduler = StdSchedulerFactory().getScheduler()
        scheduler1.start()
        scheduler1.scheduleJob(job1, trigger1)
    }
}
