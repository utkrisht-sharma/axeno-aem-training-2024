package com.assignment.core.jobs;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JobConsumer implementation that handles logging jobs.
 */
@Component(service = JobConsumer.class, immediate = true, property = {
        JobConsumer.PROPERTY_TOPICS + "=com/assignment/job/logging"
})
public class LoggingJob implements JobConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingJob.class);

    /**
     * Processes the given job and logs its ID.
     */
    @Override
    public JobResult process(Job job) {
        LOG.info("Executing Sling Job {}", job.getId());
        return JobResult.OK;
    }
}
