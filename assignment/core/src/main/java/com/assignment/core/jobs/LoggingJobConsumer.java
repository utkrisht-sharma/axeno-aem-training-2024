package com.assignment.core.jobs;

import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = JobConsumer.class,
        immediate = true,
        property = {
                JobConsumer.PROPERTY_TOPICS + "=job/logging"
        }
)
public class LoggingJobConsumer implements JobConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingJobConsumer.class);

    @Override
    public JobResult process(Job job) {
        LOG.info("Executing Sling Job with ID: {}", job.getId());
        String property = (String) job.getProperty("job");
        LOG.info("Job : {}", property);
        return JobResult.OK;
    }
}
