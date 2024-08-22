package com.assignment.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Message Logging Scheduler")
public @interface MessageLoggingSchedulerConfiguration {

    @AttributeDefinition(name = "Scheduler Name", type = AttributeType.STRING) String schedulerName() default "Logging Message Scheduler";

    @AttributeDefinition(name = "Enabled", type = AttributeType.BOOLEAN) boolean enabled() default true;

    @AttributeDefinition(name = "Cron Expression", type = AttributeType.STRING) String cronExpression() default "0 0/2 * * * ?";

}
