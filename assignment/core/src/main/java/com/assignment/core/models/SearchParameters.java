package com.assignment.core.models;

/**
 * POJO class representing the search parameters.
 * This class is used to encapsulate the input parameters for a search operation.
 */
public class SearchParameters {

    // Path where the search will be conducted
    private String path;

    // First property name to search for
    private String propertyOne;

    // Value of the first property
    private String propertyOneValue;

    // Second property name to search for
    private String propertyTwo;

    // Value of the second property
    private String propertyTwoValue;

    // Getters and Setters

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPropertyOne() {
        return propertyOne;
    }

    public void setPropertyOne(String propertyOne) {
        this.propertyOne = propertyOne;
    }

    public String getPropertyOneValue() {
        return propertyOneValue;
    }

    public void setPropertyOneValue(String propertyOneValue) {
        this.propertyOneValue = propertyOneValue;
    }

    public String getPropertyTwo() {
        return propertyTwo;
    }

    public void setPropertyTwo(String propertyTwo) {
        this.propertyTwo = propertyTwo;
    }

    public String getPropertyTwoValue() {
        return propertyTwoValue;
    }

    public void setPropertyTwoValue(String propertyTwoValue) {
        this.propertyTwoValue = propertyTwoValue;
    }
}

