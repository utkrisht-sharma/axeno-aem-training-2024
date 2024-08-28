package com.assignment.core.models;

import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Model to represent the student's marks details.
 * It reads data from a child resource named 'marksDetails'.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class StudentMarksModel {

    private static final Logger LOG = LoggerFactory.getLogger(StudentMarksModel.class);

    @ChildResource(name = "marksDetails")
    private Resource subjectsResource;

    private List<Subject> subjects = new ArrayList<>();

    /**
     * Initializes the model by populating the list of subjects from the child resources.
     * Validates that marks are between 0 and 100.
     */
    @PostConstruct
    protected void init() {
        LOG.info("Initializing StudentMarksModel.");
        if (subjectsResource != null) {
            LOG.info("Found 'marksDetails' child resource. Processing child nodes.");
            for (Resource child : subjectsResource.getChildren()) {
                String subjectName = child.getValueMap().get("subjectName", String.class);
                Float marks = child.getValueMap().get("marks", Float.class);

                if (isValidMarks(marks)) {
                    if (subjectName != null) {
                        subjects.add(new Subject(subjectName, marks));
                    } else {
                        LOG.warn("Skipping child node due to missing subject name.");
                    }
                } else {
                    LOG.warn("Skipping child node due to invalid marks: {}", marks);
                }
            }
        } else {
            LOG.error("'marksDetails' resource is null. No subjects to process.");
        }
    }

    /**
     * Checks if the given marks are within the valid range (0-100).
     */
    private boolean isValidMarks(Float marks) {
        return marks != null && marks >= 0 && marks <= 100;
    }

    /**
     * Gets the list of subjects and their marks.
     */
    public List<Subject> getSubjects() {
        return subjects;
    }
}
