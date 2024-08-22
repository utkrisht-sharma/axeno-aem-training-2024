package com.assignment.core.schedulers;

import com.assignment.core.services.JobTriggerService;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The JobScheduler class is a scheduled task that triggers a job at a fixed interval.
 * It implements the Runnable interface.
 */
@Component(service = Runnable.class, immediate = true)
public class JobScheduler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(JobScheduler.class);

    @Reference
    private Scheduler scheduler;

    @Reference
    private JobTriggerService jobTriggerService;

    /**
     * Executes the scheduled job. This method is called according to the defined schedule.
     */
    @Override
    public void run() {
        LOG.info("Scheduler is triggering job...");
        jobTriggerService.trigger();
    }

    /**
     * Activates the scheduler component and schedules the job to run at a fixed interval.
     */
    @Activate
    protected void activate() {
        ScheduleOptions scheduleOptions = scheduler.EXPR("*/10 * * * * ?");
        scheduler.schedule(this, scheduleOptions);
        LOG.info("Scheduler activated with fixed delay.");
    }

    /**
     * Deactivates the scheduler component and unschedules the job.
     */
    @Deactivate
    protected void deactivate() {
        scheduler.unschedule(this.toString());
        LOG.info("Scheduler deactivated.");
    }
}
