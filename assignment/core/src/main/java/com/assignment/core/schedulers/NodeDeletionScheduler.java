package com.assignment.core.schedulers;

import com.assignment.core.config.NodeDeletionConfiguration;
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

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = NodeDeletionConfiguration.class)

/**
 * Scheduler component for periodically deleting nodes based on the configuration.
 */
public class NodeDeletionScheduler implements Runnable {


    private String nodePath;
    private String schedulerName;
    private boolean schedulerEnabled = false;
    private static final Logger log = LoggerFactory.getLogger(NodeDeletionScheduler.class);
    @Reference
    private Scheduler scheduler;

    @Reference
    private NodeDeletionService nodeDeletionService;


    /**
     * Activates the scheduler with the configuration settings.
     */
    @Activate
    public void init(NodeDeletionConfiguration config) {
        schedulerEnabled = config.enabled();
        schedulerName = config.schedulerName();
        if (schedulerEnabled) {
            nodePath = config.nodePath();
            ScheduleOptions options = scheduler.EXPR(config.cronExpression());
            options.name(schedulerName);
            scheduler.schedule(this, options);
            log.info("{} scheduler is enabled. ", config.schedulerName());

        } else {
            log.error("{} Scheduler Is Not Enabled. ", config.schedulerName());
        }
    }

    /**
     * Deactivates the scheduler and unschedules it if it was enabled.
     */
    @Deactivate
    public void deactivate() {
        if (schedulerEnabled) {
            log.info("Unscheduling The Task");
            scheduler.unschedule(schedulerName);
        }
    }

    /**
     * Runs the scheduled job, triggering node deletion at the specified path.
     */
    @Override
    public void run() {
        log.info("Node Deletion Job Triggered");
        nodeDeletionService.deleteNode(nodePath);
    }
}
