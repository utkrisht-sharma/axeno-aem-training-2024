package com.assignment.core.config;


import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Deletion Scheduler Service ", description = "node deletion scheduler")
public @interface NodeDeletionSchedulerConfiguration {
    @AttributeDefinition(
            name = "Scheduler name",
            type = AttributeType.STRING
    )String schedulerName() default "";

    @AttributeDefinition(
            name = "Enabled",
            type = AttributeType.BOOLEAN
    )boolean enabled() default true;

    @AttributeDefinition(
            name = "Cron Expression",
            type = AttributeType.STRING
    )String cronExpression() default "";

}
