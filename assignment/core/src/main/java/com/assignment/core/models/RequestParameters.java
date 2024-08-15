package com.assignment.core.models;


public class RequestParameters {
    private String searchPath;
    private String propertyOne;
    private String propertyOneValue;
    private String propertyTwo;
    private String propertyTwoValue;
    private String saveParam;

    public RequestParameters(String searchPath, String propertyOne, String propertyOneValue, String propertyTwo, String propertyTwoValue, String saveParam) {
        this.searchPath = searchPath;
        this.propertyOne = propertyOne;
        this.propertyOneValue = propertyOneValue;
        this.propertyTwo = propertyTwo;
        this.propertyTwoValue = propertyTwoValue;
        this.saveParam = saveParam;
    }

    public String getSearchPath() {
        return searchPath;
    }

    public String getPropertyOne() {
        return propertyOne;
    }

    public String getPropertyOneValue() {
        return propertyOneValue;
    }

    public String getPropertyTwo() {
        return propertyTwo;
    }

    public String getPropertyTwoValue() {
        return propertyTwoValue;
    }

    public String getSaveParam() {
        return saveParam;
    }
}
