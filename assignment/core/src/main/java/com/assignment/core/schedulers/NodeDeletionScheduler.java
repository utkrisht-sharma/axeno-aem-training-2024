package com.assignment.core.schedulers;

import com.assignment.core.config.NodeDeletionConfig;
import com.assignment.core.services.NodeDeletionService;
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
 * Scheduler class responsible for scheduling the NodeDeletionService.
 */
@Component(immediate = true)
@Designate(ocd = NodeDeletionConfig.class)
public class NodeDeletionScheduler {

    private static final Logger log = LoggerFactory.getLogger(NodeDeletionScheduler.class);

    @Reference
    private Scheduler scheduler;

    @Reference
    private NodeDeletionService nodeDeletionService;

    private String schedulerName;
    private boolean schedulerEnabled;

    /**
     * Activates the component and schedules the node deletion job if enabled.
     */
    @Activate
    protected void activate(NodeDeletionConfig config) {
        this.schedulerName = config.schedulerName();
        this.schedulerEnabled = config.schedulerEnabled();
        String schedulerExpression = config.schedulerExpression();

        if (schedulerEnabled) {
            ScheduleOptions scheduleOptions = scheduler.EXPR(schedulerExpression);
            scheduleOptions.name(schedulerName);
            scheduler.schedule(nodeDeletionService, scheduleOptions);
            log.info("Node deletion scheduler scheduled with expression: {}", schedulerExpression);
        } else {
            log.info("Node deletion scheduler is disabled.");
        }
    }

    /**
     * Deactivates the component and unschedules the job if it was previously scheduled.
     */
    @Deactivate
    protected void deactivate(NodeDeletionConfig config) {
        if (schedulerEnabled) {
            scheduler.unschedule(schedulerName);
            log.info("Node deletion scheduler unscheduled.");
        }
    }
}
