package com.assignment.core.services.impl;

import com.assignment.core.services.JobTriggerService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.event.jobs.JobManager;


/**
 * Implementation of JobTriggerService that triggers a Sling job using the JobManager.
 */
@Component(service = JobTriggerService.class, immediate = true)
public class JobTriggerServiceImpl implements JobTriggerService {

    private static final Logger log = LoggerFactory.getLogger(JobTriggerServiceImpl.class);

    @Reference
    private JobManager jobManager;

    private static final String JOB_TOPIC = "loggingMessage";

    /**
     * Triggers a Sling job with the specified topic.
     */
    @Override
    public void triggerJob() {
        jobManager.addJob(JOB_TOPIC, null);
        log.info("Message Logging Job triggered with topic: {}", JOB_TOPIC);
    }


}
