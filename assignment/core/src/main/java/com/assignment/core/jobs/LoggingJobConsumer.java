package com.assignment.core.jobs;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A JobConsumer that handles jobs with the topic "loggingMessage".
 */

@Component(immediate = true, service = JobConsumer.class, property = {JobConsumer.PROPERTY_TOPICS + "=loggingMessage"})
public class LoggingJobConsumer implements JobConsumer {

    private static final Logger log = LoggerFactory.getLogger(LoggingJobConsumer.class);

    /**
     * Processes the job and logs its execution.
     */
    @Override
    public JobResult process(Job job) {
        log.info("Message Logging Job executed: {}", job.getTopic());
        return JobResult.OK;
    }
}
