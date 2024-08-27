package com.assignment.core.models;

public class PageData {
    private String title;
    private String path;
    private String description;
    private String tags;
    private String thumbnail;

    public PageData(String title, String path, String description, String tags, String thumbnail) {
        this.title = title;
        this.path = path;
        this.description = description;
        this.tags = tags;
        this.thumbnail = thumbnail;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
