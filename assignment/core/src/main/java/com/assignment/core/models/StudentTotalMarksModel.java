package com.assignment.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a model for student entries and their total marks.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class StudentTotalMarksModel {

    /**
     * The child resource containing individual student entries.
     */
    @ChildResource(name = "studentItem")
    private List<Resource> studentEntries;

    /**
     * The list of StudentEntry objects populated from the child resources.
     */
    private List<StudentEntry> studentEntryList = new ArrayList<>();

    /**
     * Initializes the model by adapting child resources to StudentEntry objects.
     */
    @PostConstruct
    protected void init() {
        if (studentEntries != null) {
            for (Resource studentEntryResource : studentEntries) {
                StudentEntry entry = studentEntryResource.adaptTo(StudentEntry.class);
                if (entry != null) {
                    studentEntryList.add(entry);
                }
            }
        }
    }

    /**
     * Gets the list of student entries.
     */
    public List<StudentEntry> getStudentEntries() {
        return studentEntryList;
    }
}
