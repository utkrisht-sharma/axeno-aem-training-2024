package com.assignment.core.jobs;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Consumer for handling logging jobs.
 *
 */
@Component(
        service = JobConsumer.class,
        immediate = true,
        property = {
                JobConsumer.PROPERTY_TOPICS + "=job/logging"
        }
)
public class LoggingJobConsumer implements JobConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingJobConsumer.class);

    /**
     * Processes a job.
     *
     * @param job job to process.
     * @return The result of job processing.
     */
    @Override
    public JobResult process(Job job) {
        try {
            LOG.info("Executing Sling Job with ID: {}", job.getId());
            LOG.info("Job Topic: {}", job.getTopic());
            String property = (String) job.getProperty("job");
            LOG.info("property of job : {}", property);
            return JobResult.OK;
        }catch (Exception e){
            LOG.error("Exception occured while calling logging job : {}", e.getMessage());
            return JobResult.FAILED;
        }
    }
}
