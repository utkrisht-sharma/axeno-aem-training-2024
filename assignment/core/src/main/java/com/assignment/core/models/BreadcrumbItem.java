package com.assignment.core.models;

public class BreadcrumbItem {
    private final String title;
    private final String url;
    private final boolean active;

    public BreadcrumbItem(String title, String url, boolean active) {
        this.title = title;
        this.url = url;
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public boolean isActive() {
        return active;
    }
}