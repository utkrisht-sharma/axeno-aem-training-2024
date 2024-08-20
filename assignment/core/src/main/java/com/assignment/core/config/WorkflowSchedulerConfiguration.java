package com.assignment.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Workflow Scheduler Configuration", description = "Workflow Scheduler Configuration")
public @interface WorkflowSchedulerConfiguration {

    /**
     * This method will return the name of the Scheduler
     *
     * @return {@link String}
     */
    @AttributeDefinition(name = "Scheduler name", description = "Name of the scheduler", type = AttributeType.STRING)
    public String schedulerName() default "Workflow Scheduler configuration";

    /**
     * This method will set flag to enable the scheduler
     *
     * @return {@link Boolean}
     */

    @AttributeDefinition(name = "Enabled", description = "True, if scheduler service is enabled", type = AttributeType.BOOLEAN)
    public boolean enabled() default true;

    /**
     * This method returns the Cron expression which will decide how the scheduler
     * will run
     *
     * @return {@link String}
     */

    @AttributeDefinition(name = "Cron Expression", description = "Cron expression used by the scheduler", type = AttributeType.STRING)
    public String cronExpression() default "0 0 12 1/1 * ? *";

    /**
     * This method returns the csv Path
     *
     *
     * @return {@link String}
     */

    @AttributeDefinition(name = "CSV Path", description = "CSV Path", type = AttributeType.STRING)
    public String csvPath() default "/content/dam/assignment/data.csv";

    @AttributeDefinition(name = "Workflow Model", description = "Workflow Model", type = AttributeType.STRING)
    public String model() default "/var/workflow/models/create-pages";

}