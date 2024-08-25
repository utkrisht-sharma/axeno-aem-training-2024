package com.assignment.core.models;

/**
 * The {@code BreadcrumbItem} class represents a single item in a breadcrumb trail.
 * Each breadcrumb item consists of a title and a path.
 */
public class BreadcrumbItem {

    /**
     * The title of the breadcrumb item, typically the name of the page.
     */
    private final String title;

    /**
     * The path of the breadcrumb item, typically the URL path of the page.
     */
    private final String path;

    /**
     * Constructs a new {@code BreadcrumbItem} with the specified title and path.
     *
     * @param title the title of the breadcrumb item
     * @param path  the path of the breadcrumb item
     */
    public BreadcrumbItem(String title, String path) {
        this.title = title;
        this.path = path;
    }

    /**
     * Returns the title of the breadcrumb item.
     *
     * @return the title of the breadcrumb item
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the path of the breadcrumb item.
     *
     * @return the path of the breadcrumb item
     */
    public String getPath() {
        return path;
    }
}
