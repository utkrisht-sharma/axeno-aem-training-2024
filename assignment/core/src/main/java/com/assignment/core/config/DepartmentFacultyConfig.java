package com.assignment.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * OSGi factory configuration for mapping departments to their respective faculty names.
 * This configuration interface allows defining multiple instances, each representing a department
 * and its associated faculty members.
 */
@ObjectClassDefinition(
        name = "Department Faculty Configuration",
        description = "Factory configuration for departments and their respective faculty names"
)
public @interface DepartmentFacultyConfig {

    /**
     * Specifies the name of the department.
     * This is a required field in the configuration and represents the department for which faculty names are configured.
     *
     * @return the name of the department
     */
    @AttributeDefinition(
            name = "Department",
            description = "Name of the department"
    )
    String department();

    /**
     * Specifies an array of faculty names associated with the department.
     * This array can contain one or more faculty names and is used to link faculty members to the specified department.
     *
     * @return an array of faculty names for the department
     */
    @AttributeDefinition(
            name = "Faculty Names",
            description = "Array of faculty names for the department"
    )
    String[] facultyNames();
}
