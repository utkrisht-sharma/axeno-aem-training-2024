package com.assignment.core.services;


import com.assignment.core.services.impl.JobTriggerService;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@Component(service= JobTriggerService.class)
public class JobTriggerServiceImpl implements JobTriggerService {

    private static final Logger LOG = LoggerFactory.getLogger(JobTriggerServiceImpl.class);

    @Reference
    private JobManager jobManager;

    @Activate
    public void trigger() {
        LOG.info("Triggering Sling Job...");
        Map<String, Object> jonProperties = new HashMap<>();
        jonProperties.put("job" , "logging job");
        jobManager.addJob("job/logging", jonProperties);

    }
}
