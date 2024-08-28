package com.assignment.core.models;

/**
 * Represents a subject with its associated marks and result.
 */
public class Subject {
    private String name;
    private float marks;
    private String result;

    /**
     * Constructs a Subject with the specified name and marks.
     * The result is automatically determined based on the marks.
     */
    public Subject(String name, float marks) {
        this.name = name;
        this.marks = marks;
        this.result = (marks >= 40) ? "Pass" : "Fail";
    }

    /**
     * Gets the name of the subject.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the marks obtained in the subject.
     */
    public float getMarks() {
        return marks;
    }

    /**
     * Gets the result of the subject based on the marks.
     */
    public String getResult() {
        return result;
    }
}
