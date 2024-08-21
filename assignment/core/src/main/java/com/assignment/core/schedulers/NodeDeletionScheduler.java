package com.assignment.core.schedulers;

import com.assignment.core.config.NodeDeletionSchedulerConfiguration;
import org.apache.sling.api.resource.*;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A scheduler service for periodically deleting a node under a specified path.
 */
@Component(service = Runnable.class, immediate = true)
@Designate(ocd = NodeDeletionSchedulerConfiguration.class)
public class NodeDeletionScheduler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(NodeDeletionScheduler.class);
    private static final String NODE_PATH = "/content/assignment/new-page-2";  // Path to the node to be deleted

    @Reference
    private Scheduler scheduler;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private int schedulerId;

    /**
     * Activates the Node Deletion Scheduler based on the provided configuration.
     *
     * @param config The configuration for the scheduler.
     */
    @Activate
    @Modified
    public void activate(NodeDeletionSchedulerConfiguration config) {
        LOG.info("Activating Node Deletion Scheduler with configuration: {}", config);
        this.schedulerId = config.schedulerName().hashCode();
        addScheduler(config);
    }

    /**
     * Deactivates the scheduler by removing it.
     */
    @Deactivate
    protected void deactivate() {
        LOG.info("Deactivating Node Deletion Scheduler.");
        removeScheduler();
    }

    /**
     * Adds the scheduler based on the configuration.
     *
     * @param config The configuration for the scheduler.
     */
    private void addScheduler(NodeDeletionSchedulerConfiguration config) {
        boolean enabled = config.enabled();
        if (enabled) {
            ScheduleOptions scheduleOptions = scheduler.EXPR(config.cronExpression());
            scheduleOptions.name(String.valueOf(schedulerId));
            scheduleOptions.canRunConcurrently(false);
            scheduler.schedule(this, scheduleOptions);
            LOG.info("Node Deletion Scheduler added successfully with name: {}", schedulerId);

            // Immediately trigger the job upon activation
            ScheduleOptions scheduleOptionsNow = scheduler.NOW();
            scheduler.schedule(this, scheduleOptionsNow);
        } else {
            LOG.info("Node Deletion Scheduler is disabled.");
        }
    }

    /**
     * Removes the scheduler
     */
    private void removeScheduler() {
        LOG.info("Removing Node Deletion Scheduler with name: {}", schedulerId);
        scheduler.unschedule(String.valueOf(schedulerId));
    }

    /**
     * Executes the scheduled task of deleting a child node.
     * deletes one child node under the specified path at each run.
     */
    @Override
    public void run() {
        Map<String, Object> authInfo = new HashMap<>();
        authInfo.put(ResourceResolverFactory.SUBSERVICE, "nodeservice");

        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authInfo)) {
            Resource resource = resourceResolver.getResource(NODE_PATH);

            if (resource != null) {
                Iterator<Resource> childResources = resource.listChildren();
                if (childResources.hasNext()) {
                    Resource childResource = childResources.next();
                    resourceResolver.delete(childResource);
                    LOG.info("Deleted child node: {}", childResource.getPath());
                } else {
                    LOG.warn("No child nodes found under: {}", NODE_PATH);
                }
                resourceResolver.commit();
            } else {
                LOG.warn("Node not found: {}", NODE_PATH);
            }
        }catch (LoginException e) {
            LOG.error("Failed to obtain ResourceResolver due to login error: {}", e.getMessage(), e);
        }catch (PersistenceException e) {
            LOG.error("Failed to commit changes: {}", NODE_PATH, e);
        }
    }
}
