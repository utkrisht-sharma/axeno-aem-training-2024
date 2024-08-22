package com.assignment.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Deletion Node Scheduler Configuration")
public @interface NodeDeletionConfiguration {

    @AttributeDefinition(name = "Scheduler Name", type = AttributeType.STRING) String schedulerName() default "Node Deletion Scheduler";

    @AttributeDefinition(name = "Enabled", type = AttributeType.BOOLEAN) boolean enabled() default true;

    @AttributeDefinition(name = "Cron Expression", type = AttributeType.STRING) String cronExpression() default "0 0/2 * * * ?";

    @AttributeDefinition(name = "Node Path", type = AttributeType.STRING) String nodePath() default "/content/assignment/temp";
}
