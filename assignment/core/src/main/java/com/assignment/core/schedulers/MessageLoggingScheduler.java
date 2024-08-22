package com.assignment.core.schedulers;

import com.assignment.core.config.MessageLoggingSchedulerConfiguration;
import com.assignment.core.services.JobTriggerService;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Scheduler component that triggers the Message Logging Job at specified intervals.
 */
@Component(service = Runnable.class, immediate = true)
@Designate(ocd = MessageLoggingSchedulerConfiguration.class)
public class MessageLoggingScheduler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(MessageLoggingScheduler.class);
    String schedulerName;
    boolean schedulerEnabled = false;

    @Reference
    Scheduler scheduler;

    @Reference
    JobTriggerService triggerService;

    /**
     * Activates the scheduler with the configuration settings.
     */
    @Activate
    public void init(MessageLoggingSchedulerConfiguration config) {
        schedulerName = config.schedulerName();
        if (config.enabled()) {
            ScheduleOptions options = scheduler.EXPR(config.cronExpression());
            options.name(config.schedulerName());
            scheduler.schedule(this, options);
            log.info("{} scheduler is enabled. ", schedulerName);

        } else {
            log.error("{} Scheduler Is Not Enabled. ", schedulerName);
        }
    }

    /**
     * Deactivates the scheduler and unschedules it if it was enabled.
     */
    @Deactivate
    public void deactivate() {
        if (schedulerEnabled) {
            scheduler.unschedule(schedulerName);
        }


    }

    /**
     * Runs the scheduled job, triggering the Message Logging Job.
     */
    @Override
    public void run() {
        log.info("Message Logging Job Triggered");
        triggerService.triggerJob();
    }
}
