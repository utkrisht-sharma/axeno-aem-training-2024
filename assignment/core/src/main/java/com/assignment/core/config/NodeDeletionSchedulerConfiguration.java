package com.assignment.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;

import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
/**
 * Configuration for the Node Deletion Scheduler.
 * This interface defines the configuration properties for the scheduler.
 */

@ObjectClassDefinition(name = "Node Deletion Scheduler Configuration", description = "Configuration for Node Deletion Scheduler",pid = "com.assignment.core.config.NodeDeletionSchedulerConfiguration")
public @interface NodeDeletionSchedulerConfiguration {

    @AttributeDefinition(
            name = "Scheduler Name",
            description = " Name Of the scheduler",
            type = AttributeType.STRING

    )
    String schedulerName() default "NodeDeletionScheduler";

    @AttributeDefinition(
            name = "Enabled",
            description = "Enable or disable the scheduler",
            type = AttributeType.BOOLEAN

    )
    boolean enabled() default true;

    @AttributeDefinition(
            name = "Cron Expression",
            description = "Cron expression defining the schedule",
            type = AttributeType.STRING

    )
    String cronExpression() default "0 0/1 * 1/1 * ? *"; // every minute by default
}
