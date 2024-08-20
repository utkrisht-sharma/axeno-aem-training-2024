
package com.assignment.core.schedulers;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.model.WorkflowModel;
import com.assignment.core.config.WorkflowSchedulerConfiguration;
import com.google.common.base.Strings;

import java.util.HashMap;
import java.util.Map;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = WorkflowSchedulerConfiguration.class)
public class WorkflowScheduler implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(WorkflowScheduler.class);

    @Reference
    private Scheduler scheduler;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private String csvPath;
    private String schedulerName;
    private String model;

    @Activate
    private void activate(WorkflowSchedulerConfiguration configuration) {
        logger.info("Activating Workflow Scheduler with configuration: {}", configuration);
        this.csvPath = configuration.csvPath();
        this.schedulerName = configuration.schedulerName();
        this.model = configuration.model();
        addScheduler(configuration);
    }

    @Modified
    protected void modified(WorkflowSchedulerConfiguration configuration) {
        logger.info("Modifying Workflow Scheduler with new configuration: {}", configuration);
        removeScheduler(configuration);
        this.csvPath = configuration.csvPath();
        this.schedulerName = configuration.schedulerName();
        this.model = configuration.model();
        addScheduler(configuration);
    }

    @Deactivate
    protected void deactivated(WorkflowSchedulerConfiguration configuration) {
        logger.info("Deactivating Workflow Scheduler.");
        removeScheduler(configuration);
    }

    private void addScheduler(WorkflowSchedulerConfiguration configuration) {
        boolean enabled = configuration.enabled();
        if (enabled) {
            ScheduleOptions scheduleOptions = scheduler.EXPR(configuration.cronExpression());
            if (!Strings.isNullOrEmpty(schedulerName)) {
                scheduleOptions.name(schedulerName);
                scheduleOptions.canRunConcurrently(false);
                scheduler.schedule(this, scheduleOptions);
                logger.info("Workflow Scheduler added successfully with name: {}", schedulerName);
                ScheduleOptions scheduleOptionsNow = scheduler.NOW();
                scheduler.schedule(this,scheduleOptionsNow);
            } else {
                logger.warn("Scheduler name is empty or null. Cannot schedule.");
            }
        } else {
            logger.info("Workflow Scheduler is in disabled state.");
        }
    }

    private void removeScheduler(WorkflowSchedulerConfiguration configuration) {
        logger.info("Removing Workflow Scheduler with name: {}", schedulerName);
        scheduler.unschedule(schedulerName);
    }

    @Override
    public void run() {
        ResourceResolver resourceResolver = null;
        try {
            // Get a resource resolver with appropriate service user mapping
            Map<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, "workflowservice");
            resourceResolver = resourceResolverFactory.getServiceResourceResolver(param);

            if (resourceResolver != null) {
                logger.info("ResourceResolver obtained successfully.");

                WorkflowSession workflowSession = resourceResolver.adaptTo(WorkflowSession.class);
                if (workflowSession != null) {
                    WorkflowModel workflowModel = workflowSession.getModel(model);
                    if (workflowModel != null) {
                        WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH", csvPath);
                        workflowSession.startWorkflow(workflowModel, workflowData);
                        logger.info("Workflow started successfully with model: {} for path: {}", workflowModel.getTitle(), csvPath);
                    } else {
                        logger.error("Workflow model not found: {}", model);
                    }
                } else {
                    logger.error("Unable to adapt ResourceResolver to WorkflowSession.");
                }
            } else {
                logger.error("Failed to obtain ResourceResolver.");
            }
        } catch (Exception e) {
            logger.error("Error during workflow execution", e);
        } finally {
            if (resourceResolver != null) {
                resourceResolver.close();
                logger.info("ResourceResolver closed.");
            }
        }
    }
}
