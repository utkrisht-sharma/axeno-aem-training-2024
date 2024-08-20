package com.assignment.core.PageMetaData;

public class PageData {
    private String title;
    private String path;
    private String description;
    private String tags;

    public PageData(String title, String path, String description, String tags) {
        this.title = title;
        this.path = path;
        this.description = description;
        this.tags = tags;
    }

    public String getTitle() {
        return title;
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public String getTags() {
        return tags;
    }
}
