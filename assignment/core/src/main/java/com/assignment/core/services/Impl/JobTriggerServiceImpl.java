package com.assignment.core.services.Impl;

import com.assignment.core.services.JobTriggerService;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the JobTriggerService interface.
 * This service is responsible for triggering a job. Currently.
 */
@Component(service = JobTriggerService.class, immediate = true)
public class JobTriggerServiceImpl implements JobTriggerService {

    private static final Logger LOG = LoggerFactory.getLogger(JobTriggerServiceImpl.class);

    @Reference
    private JobManager jobManager;

    /**
     * Triggers the job by logging a message.
     */
    @Override
    public void trigger() {
        LOG.info("Your Job is Triggered");
    }
}
