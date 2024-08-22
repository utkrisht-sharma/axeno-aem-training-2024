package com.assignment.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Node Deletion Scheduler Configuration")
public @interface NodeDeletionConfig {
    @AttributeDefinition(
            name = "Scheduler Name",
            description = "Name of the Scheduler",
            type = AttributeType.STRING)
    String schedulerName() default "Node Deletion Scheduler";

    @AttributeDefinition(
            name = "Scheduler Expression",
            description = "Cron expression for scheduler",
            type = AttributeType.STRING)
    String schedulerExpression() default "0 0/1 * 1/1 * ? *";

    @AttributeDefinition(
            name = "Scheduler Enabled",
            description = "Is Scheduler enabled?",
            type = AttributeType.BOOLEAN)
    boolean schedulerEnabled() default true;

    @AttributeDefinition(
            name = "Root Path",
            description = "Root path from which nodes will be deleted",
            type = AttributeType.STRING)
    String rootPath() default "/content/assignment/home";
}
