package com.assignment.core.schedulers;


import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * A scheduler component that triggers a periodic job.
 * This job logs a message at scheduled intervals and triggers
 * the JobTriggerService to perform additional actions.
 */
@Component(service = Runnable.class, immediate = true)
public class LoggerJobScheduler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(LoggerJobScheduler.class);

    @Reference
    private Scheduler scheduler;
    @Reference
    private JobManager jobManager;


    private String jobId; // To store the job ID for unscheduling

    /**
     * Activates the scheduler component by setting up the schedule.
     * The scheduler is configured to run every hour at the start of the hour.
     */
    @Activate
    protected void activate() {
        ScheduleOptions scheduleOptions = scheduler.EXPR("* * * ? * *");
        jobId = String.valueOf(scheduler.schedule(this, scheduleOptions));
        logger.info("LoggerJobScheduler activated and scheduled to run e.");
    }

    /**
     * Runs the scheduled job.
     * Logs a message indicating that the scheduler has triggered and
     * calls the JobTriggerService to execute the periodic job.
     */
    @Override
    public void run() {
        logger.info("Scheduler triggered - running job...");
        triggerPeriodicLoggerJob();

    }

    protected void deactivate() {
        if (jobId != null) {
            scheduler.unschedule(jobId);
            logger.info("LoggerJobScheduler deactivated and job unscheduled.");
        }
    }

    public void triggerPeriodicLoggerJob() {

        Map<String, Object> jobProperties = new HashMap<>();

        jobProperties.put("JobName", "logger");

        jobManager.addJob("com/assignment/core/jobs/periodiclogger", jobProperties);
        logger.info("Periodic Logger Job triggered successfully.");


    }
}