package com.assignment.core.jobs;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link JobConsumer} implementation that processes jobs with the topic "job/custom".
 * This class handles custom jobs by logging information when a job is executed.
 */
@Component(
        service = JobConsumer.class,
        immediate = true,
        property = {
                JobConsumer.PROPERTY_TOPICS + "=job/custom"
        }
)
public class PeriodicLoggingJobConsumer implements JobConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(PeriodicLoggingJobConsumer.class);

    /**
     * Processes the given job by logging its execution and properties.
     * @param job The {@link Job} object to be processed. Contains details and properties of the job.
     * @return {@link JobResult#OK} indicating successful processing of the job.
     */
    @Override
    public JobResult process(Job job) {
        LOG.info("Custom job executed!");
        LOG.info("Received property: {}", job.getProperty("message"));
        return JobResult.OK;
    }
}
