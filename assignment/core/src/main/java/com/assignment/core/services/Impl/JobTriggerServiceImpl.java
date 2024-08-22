package com.assignment.core.services.Impl;

import com.assignment.core.services.JobTriggerService;
import org.apache.sling.event.jobs.JobManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.HashMap;

/**
 * Service to schedule and trigger the Periodic Logger Job.
 */
@Component(immediate = true, service = JobTriggerService.class)
public class JobTriggerServiceImpl implements JobTriggerService {

    private static final Logger LOG = LoggerFactory.getLogger(JobTriggerServiceImpl.class);

    @Reference
    private JobManager jobManager;

    /**
     * Triggers the Periodic Logger Job.
     * This method adds a job to the {@link JobManager} to execute the periodic logger job.
     */
    @Override
    public void triggerPeriodicLoggerJob() {
        try {
            jobManager.addJob("com/assignment/core/jobs/periodiclogger", new HashMap<>());
            LOG.info("Periodic Logger Job triggered successfully.");
        } catch (Exception e) {
            LOG.error("Failed to trigger Periodic Logger Job.", e);
        }
    }
}
