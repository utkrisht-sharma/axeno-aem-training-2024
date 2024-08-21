package com.assignment.core.services;

import com.assignment.core.services.impl.JobTriggerService;
import org.apache.sling.event.jobs.JobBuilder;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the JobTriggerService for triggering Sling Jobs.
 */
@Component(name = "Job Triggering Service", service = JobTriggerService.class, immediate = true)
public class JobTriggerServiceImpl implements JobTriggerService {

    private static final Logger LOG = LoggerFactory.getLogger(JobTriggerServiceImpl.class);

    @Reference
    private JobManager jobManager;

    /**
     * Activates the service and triggers a Sling Job.
     */
    @Activate
    public void trigger() {
        LOG.info("Triggering Sling Job...");
        JobBuilder jobBuilder = jobManager.createJob("job/logging");
        jobBuilder.add();
    }
}
