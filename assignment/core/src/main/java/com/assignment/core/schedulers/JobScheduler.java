package com.assignment.core.schedulers;

import com.assignment.core.services.JobTriggerService;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A scheduler service that triggers a job at specified intervals.
 *
 */
@Component(
        name = "Job Scheduler Service",
        service = Runnable.class,
        immediate = true
)
@Designate(ocd = JobScheduler.JobSchedulerConfiguration.class)
public class JobScheduler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    /**
     * Configuration interface for the Job Scheduler.
     */
    @ObjectClassDefinition(
            name = "Job Scheduler Configuration",
            description = "Configuration for a logging job scheduler"
    )
    public static @interface JobSchedulerConfiguration {

        /**
         * @return The name of the scheduler.
         */
        @AttributeDefinition(
                name = "Scheduler Name",
                type = AttributeType.STRING
        )
        String schedulerName() default "Job Scheduler";

        /**
         * @return Whether the scheduler is enabled or not.
         */
        @AttributeDefinition(
                name = "Enabled",
                type = AttributeType.BOOLEAN
        )
        boolean enabled() default false;

        /**
         * @return The cron expression that defines the schedule.
         */
        @AttributeDefinition(
                name = "Cron Expression",
                type = AttributeType.STRING
        )
        String cronExpression() default "0 0/1 * 1/1 * ? *";
    }

    @Reference
    private Scheduler scheduler;

    private int schedulerId;

    @Reference
    private JobTriggerService jobTriggerService;

    /**
     * Activates the scheduler based on the provided configuration.
     *
     * @param config The scheduler configuration.
     */
    @Activate
    protected void activate(JobSchedulerConfiguration config) {
        if (config.enabled()) {
            ScheduleOptions scheduleOptions = scheduler.EXPR(config.cronExpression());
            schedulerId = config.schedulerName().hashCode();
            scheduler.schedule(this, scheduleOptions);
            logger.info("Scheduler enabled with configuration: {}", config);
        } else {
            logger.error("Scheduler is in a disabled state");
        }
    }

    /**
     * Deactivates the scheduler.
     */
    protected void deactivate() {
        scheduler.unschedule(String.valueOf(schedulerId));
    }

    @Override
    public void run() {
        logger.info("Scheduler triggered - running job...");
        jobTriggerService.trigger();
    }
}
