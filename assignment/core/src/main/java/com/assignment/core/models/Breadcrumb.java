package com.assignment.core.models;

public class Breadcrumb {
    private String name;
    private String path;

    public Breadcrumb(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
