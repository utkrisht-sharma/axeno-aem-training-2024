package com.assignment.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Represents a student entry model in the system.
 */
@Model(adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
      )
public class StudentEntry {

    /**
     * The name of the student.
     */
    @ValueMapValue(name = "studentName")
    private String studentName;

    /**
     * The total marks obtained by the student.
     */
    @ValueMapValue(name = "totalMarks")
    private int totalMarks;

    /**
     * Gets the name of the student.
     */
    public String getStudentName() {
        return studentName;
    }

    /**
     * Gets the total marks obtained by the student.
     */
    public int getTotalMarks() {
        return totalMarks;
    }
}
