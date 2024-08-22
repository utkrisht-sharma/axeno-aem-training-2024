package com.assignment.core.schedulers;

import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;

/**
 * A class that schedules a custom job to be triggered every minute using Sling Scheduler.
 * <p>
 * This class implements the {@link Runnable} interface and uses the Sling Scheduler
 * to periodically schedule a job with the topic "job/custom". The scheduling occurs
 * every minute as defined by the cron expression in the {@link #activate()} method.
 */
@Component(service = Runnable.class, immediate = true)
public class CustomJobScheduler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CustomJobScheduler.class);

    @Reference
    private JobManager jobManager;

    @Reference
    private Scheduler scheduler;

    private String schedulerName = "logging scheduler";

    /**
     * Activates the scheduler by setting up the schedule to trigger every minute.
     * <p>
     * This method creates a {@link ScheduleOptions} object with a cron expression
     * and schedules this task to run every minute. It is invoked after the component
     * is constructed and initialized.
     */
    @PostConstruct
    protected void activate() {
        ScheduleOptions scheduleOptions = scheduler.EXPR("0 * * * * ?");
        scheduleOptions.name(schedulerName);
        scheduler.schedule(this, scheduleOptions);
    }

    /**
     * Deactivates the scheduler by unscheduling the job.
     * <p>
     * This method is called before the component is destroyed and ensures that the
     * scheduled job is unscheduled, preventing further executions.
     */
    @PreDestroy
    protected void deactivate() {
        scheduler.unschedule(schedulerName);
    }

    /**
     * Runs the scheduled job. This method is executed by the Sling Scheduler
     * according to the defined schedule.
     * <p>
     * It triggers the {@link #scheduleJob()} method to schedule a new job
     * with the topic "job/custom".
     */
    @Override
    public void run() {
        scheduleJob();
    }

    /**
     * Schedules a new job with the topic "job/custom".
     * <p>
     * This method creates a job with a message property and adds it to the JobManager.
     * It logs a success message if the job is scheduled successfully or an error message
     * if an exception occurs.
     */
    private void scheduleJob() {
        try {
            Map<String, Object> properties = new HashMap<>();
            properties.put("message", "Scheduled job executed!");

            // Schedule the job with the topic 'job/custom'
            jobManager.addJob("job/custom", properties);
            LOG.info("Custom job scheduled successfully.");
        } catch (Exception e) {
            LOG.error("Error scheduling job: " + e.getMessage(), e);
        }
    }
}
g