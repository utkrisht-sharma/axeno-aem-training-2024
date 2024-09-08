package com.assignment.core.jobs;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * A Sling Job that logs a message periodically.
 */
@Component(immediate = true, service = JobConsumer.class, property = {
        JobConsumer.PROPERTY_TOPICS + "=com/assignment/core/jobs/periodiclogger"
})
public class PeriodicLoggerJob implements JobConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicLoggerJob.class);

    /**
     * Executes the job and logs a message.
     *
     * @param job The job to be processed
     * @return A result state indicating the job's execution status
     */
    @Override
    public JobResult process(Job job) {

        String jobName = (String) job.getProperty("JobName");

            LOGGER.info("Periodic Logger Job started at: {}", System.currentTimeMillis());
            LOGGER.info("jobName : {}", jobName);
            return JobResult.OK;




    }
}
