package com.assignment.core.schedulers;

import com.assignment.core.services.NodeDeletionService;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A scheduler that periodically triggers the deletion of nodes under a specified JCR path.
 */
@Component(
        service = Runnable.class,
        immediate = true
)
public class NodeDeletionScheduler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(NodeDeletionScheduler.class);

    @Reference
    private Scheduler scheduler;

    @Reference
    private NodeDeletionService nodeDeletionService;

    private String scheduleExpression = "* * * * *"; // Runs every 3 minutes
    private String rootPath = "/content/assignment/us/en/my-assignment-page";

    /**
     * Activates the scheduler and sets it to run according to the specified cron expression.
     */
    @Modified
    public void activate() {
        ScheduleOptions scheduleOptions = scheduler.EXPR(scheduleExpression);
        scheduleOptions.name("Node Deletion Scheduler");
        scheduleOptions.canRunConcurrently(false);
        scheduler.schedule(this, scheduleOptions);
        LOG.info("Node Deletion Scheduler activated with expression: {} and root path: {}", scheduleExpression, rootPath);
    }

    /**
     * The method executed by the scheduler. It triggers the deletion of one node at a time.
     * If no nodes are left, the scheduler is unscheduled.
     */
    @Override
    public void run() {
        LOG.info("Running Node Deletion Scheduler");
        boolean nodesRemaining = nodeDeletionService.deleteNodesUnderPath(rootPath);
        if (!nodesRemaining) {
            LOG.info("All nodes deleted. Cancelling the scheduler.");
            scheduler.unschedule("Node Deletion Scheduler");
        }
    }
}
