package com.assignment.core.services.Impl;

import com.assignment.core.config.NodeDeletionConfig;
import com.assignment.core.services.NodeDeletionService;
import org.apache.sling.api.resource.*;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Collections;
import java.util.Map;

/**
 * Implementation of the  NodeDeletionService} interface that schedules and performs node deletion tasks.
 */
@Component(service = Runnable.class, immediate = true)
@Designate(ocd = NodeDeletionConfig.class)
public class NodeDeletionServiceImpl implements Runnable, NodeDeletionService {

    private static final Logger log = LoggerFactory.getLogger(NodeDeletionServiceImpl.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private Scheduler scheduler;

    private String rootPath;
    private boolean schedulerEnabled;
    private String schedulerName;
    private String schedulerExpression;

    /**
     * Activates the component, initializes configuration settings, and schedules the node deletion job if enabled.
     */
    @Activate
    protected void activate(NodeDeletionConfig config) {
        this.rootPath = config.rootPath();
        this.schedulerEnabled = config.schedulerEnabled();
        this.schedulerName = config.schedulerName();
        this.schedulerExpression = config.schedulerExpression();

        log.info("NodeDeletionScheduler activated with rootPath: {}", rootPath);

        if (schedulerEnabled) {
            ScheduleOptions scheduleOptions = scheduler.EXPR(schedulerExpression);
            scheduleOptions.name(schedulerName);
            scheduler.schedule(this, scheduleOptions);
            log.info("Scheduler scheduled with expression: {}", schedulerExpression);
        } else {
            log.info("Scheduler is disabled.");
        }
    }

    /**
     * Deactivates the component and unschedules the job if it was previously scheduled.
     */
    @Deactivate
    protected void deactivate(NodeDeletionConfig config) {
        if (schedulerEnabled) {
            scheduler.unschedule(config.schedulerName());
            log.info("Scheduler unscheduled.");
        }
    }

    /**
     * It deletes nodes under the specified root path if the scheduler is enabled.
     */
    @Override
    public void run() {
        if (schedulerEnabled) {
            deleteNodes();
        } else {
            log.info("NodeDeletionScheduler is disabled.");
        }
    }

    /**
     * Deletes nodes under the configured root path.
     */
    @Override
    public void deleteNodes() {
        try (ResourceResolver resourceResolver = getServiceResourceResolver()) {
            Resource rootResource = resourceResolver.getResource(rootPath);
            if (rootResource != null) {
                for (Resource child : rootResource.getChildren()) {
                    try {
                        resourceResolver.delete(child);
                        log.info("Marked for deletion: {}", child.getPath());
                    } catch (PersistenceException e) {
                        log.error("Failed to mark node for deletion: {}", child.getPath(), e);
                    }
                }
                resourceResolver.commit();
                log.info("Deletion committed successfully.");
            } else {
                log.warn("Root path not found: {}", rootPath);
            }
        } catch (PersistenceException e) {
            log.error("Persistence error occurred while deleting nodes under path: {}", rootPath, e);
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting nodes under path: {}", rootPath, e);
        }
    }


    /**
     * Provides a  ResourceResolver for the node deletion service.
     */
    private ResourceResolver getServiceResourceResolver() throws LoginException {
        Map<String, Object> param = Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "nodeDeletionService");
        return resourceResolverFactory.getServiceResourceResolver(param);
    }
}
