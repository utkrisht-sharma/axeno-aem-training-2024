package com.assignment.core.schedulers;

import com.assignment.core.config.NodeDeletionSchedulerConfiguration;
import org.apache.sling.api.resource.*;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A scheduler component that manages the periodic deletion of nodes.
 * The scheduler is configured based on properties defined in
 * NodeDeletionSchedulerConfiguration and triggers the
 * NodeDeletionService to delete nodes at scheduled intervals.
 */
@Component(service = Runnable.class, immediate = true)
public class NodeDeletionScheduler implements Runnable {
    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    // Keep track of the last deleted node in a persistent way, e.g., in a config node or a file
    private static String lastDeletedNodePath = "/content/usergenerated/content"; // Starting point


    private static final Logger LOGGER = LoggerFactory.getLogger(NodeDeletionScheduler.class);

    @Reference
    private Scheduler scheduler;


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
        deleteNodes("/content/usergenerated/content");
    }

    public void deleteNodes(String rootPath) {
        Map<String, Object> authenticationMap = new HashMap<>();
        authenticationMap.put(ResourceResolverFactory.SUBSERVICE, "node-deletion-service");

        try (ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(authenticationMap)) {
            Resource rootResource = resourceResolver.getResource(rootPath);

            if (rootResource != null) {
                // Get child resources from the last deleted node path
                Iterator<Resource> childResources = rootResource.listChildren();
                boolean nodeDeleted = false;

                while (childResources.hasNext()) {
                    Resource childResource = childResources.next();
                    if (childResource.getPath().equals(lastDeletedNodePath)) {
                        // Move to the next node
                        continue;
                    }

                    deleteNode(childResource, resourceResolver);
                    lastDeletedNodePath = childResource.getPath(); // Update last deleted node path
                    nodeDeleted = true;
                    break; // Exit after deleting one node
                }

                if (nodeDeleted) {
                    resourceResolver.commit();
                }
            } else {
                LOGGER.warn("Root node not found: {}", rootPath);
            }
        } catch (PersistenceException e) {
            LOGGER.error("Failed to commit changes after deleting nodes under {}", rootPath, e);
        } catch (LoginException e) {
            LOGGER.error("Login error while accessing {}", rootPath, e);
        }
    }

    /**
     * Deletes a specific node.
     *
     * @param resource         The node to be deleted.
     * @param resourceResolver The {@link ResourceResolver} used to perform the deletion.
     */
    private void deleteNode(Resource resource, ResourceResolver resourceResolver) {
        try {
            resourceResolver.delete(resource);
            LOGGER.info("Deleted node: {}", resource.getPath());
        } catch (PersistenceException e) {
            LOGGER.error("Failed to delete node: {}", resource.getPath(), e);
        }
    }
}
