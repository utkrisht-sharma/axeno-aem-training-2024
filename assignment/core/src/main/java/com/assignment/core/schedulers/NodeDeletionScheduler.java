package com.assignment.core.schedulers;

import com.assignment.core.config.NodeDeletionSchedulerConfiguration;
import com.assignment.core.services.NodeDeletionService;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A scheduler component that manages the periodic deletion of nodes.
 * The scheduler is configured based on properties defined in
 * NodeDeletionSchedulerConfiguration and triggers the
 * NodeDeletionService to delete nodes at scheduled intervals.
 */
@Component(service = Runnable.class, immediate = true)
public class NodeDeletionScheduler implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeDeletionScheduler.class);

    @Reference
    private Scheduler scheduler;

    @Reference
    private NodeDeletionService nodeDeletionService;

    private String schedulerName;
    private boolean enabled;
    private String cronExpression;

    /**
     * Activates or updates the scheduler configuration.
     * This method sets the scheduler name, enabled status, and cron expression
     * based on the provided configuration. It schedules or unschedules the job
     * according to the enabled status.
     *
     * @param config The configuration object containing scheduler properties.
     */
    @Activate
    @Modified
    protected void activate(NodeDeletionSchedulerConfiguration config) {
        this.schedulerName = config.schedulerName();
        this.enabled = config.enabled();
        this.cronExpression = config.cronExpression();

        if (enabled) {
            addScheduler();
        } else {
            removeScheduler();
        }
    }

    /**
     * Deactivates the scheduler by removing the scheduled job.
     * This method is called when the component is deactivated or the scheduler
     * configuration is changed to disabled.
     */
    @Deactivate
    protected void deactivate() {
        removeScheduler();
    }

    /**
     * Adds or updates the scheduler with the current configuration.
     * It creates a new schedule based on the cron expression and assigns
     * the scheduler name.
     */
    private void addScheduler() {
        ScheduleOptions options = scheduler.EXPR(cronExpression);
        options.name(schedulerName);
        scheduler.schedule(this, options);
        LOGGER.info("Node Deletion Scheduler added with name: {}", schedulerName);
    }

    /**
     * Removes the scheduler from the scheduling system.
     * This method unschedules the job using the scheduler name.
     */
    private void removeScheduler() {
        scheduler.unschedule(schedulerName);
        LOGGER.info("Node Deletion Scheduler removed with name: {}", schedulerName);
    }

    /**
     * Executes the scheduled job.
     * This method is called when the scheduler triggers the job. It logs the
     * trigger event and calls the NodeDeletionService to delete nodes
     * under the specified path.
     */
    @Override
    public void run() {
        LOGGER.info("Scheduler '{}' triggered - running job...", schedulerName);
        nodeDeletionService.deleteNodes("/content/usergenerated/content");
    }
}
