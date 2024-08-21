package com.assignment.core.schedulers;


import com.assignment.core.services.impl.JobTriggerService;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Runnable.class )
public class JobScheduler implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    @Reference
    private Scheduler scheduler;

    @Reference
    JobTriggerService jobTriggerService;

    @Activate
    protected void activate(){
        ScheduleOptions scheduleOptions = scheduler.EXPR("0 * * * * ?");
        scheduler.schedule(this, scheduleOptions);
    }
    @Override
    public void run(){
        logger.info("Scheduler triggered - running job...");
        jobTriggerService.trigger();
    }
}
