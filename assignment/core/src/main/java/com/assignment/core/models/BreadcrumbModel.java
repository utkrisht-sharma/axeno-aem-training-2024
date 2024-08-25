package com.assignment.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Self;
import org.apache.sling.models.annotations.SlingObject;

import java.util.LinkedList;
import java.util.List;

/**
 * A Sling Model for rendering breadcrumbs in an AEM page.
 * This model adapts from a {@link Resource} and uses the {@link PageManager} API to build a list of breadcrumbs
 * representing the current page's navigation path up to a maximum depth of 3 levels.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BreadcrumbModel {

    @SlingObject
    private ResourceResolver resolver;

    @Self
    private Resource currentResource;

    /**
     * Retrieves the navigation path as a list of breadcrumb segments.
     * This method uses the {@link PageManager} to get the current page and traverses up the page hierarchy
     * to construct breadcrumb segments. It collects the page title and path for each breadcrumb segment,
     * stopping when it reaches a depth of 3 levels or the root of the page hierarchy.
     *
     * @return A list of {@link PathSegment} objects representing the navigation path.
     */
    public List<PathSegment> getNavigationPath() {
        List<PathSegment> segments = new LinkedList<>();

        // Get PageManager from the resolver
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        if (pageManager == null) {
            return segments; // If PageManager is not available, return empty list
        }

        // Get the current page from the resource
        Page currentPage = pageManager.getContainingPage(currentResource);
        if (currentPage == null) {
            return segments; // If current page is not found, return empty list
        }

        // Traverse up to the root or specified depth
        int depth = 0;
        while (currentPage != null && depth < 3) {
            segments.add(0, new PathSegment(currentPage.getTitle(), currentPage.getPath()));
            currentPage = currentPage.getParent();
            depth++;
        }

        return segments;
    }

    /**
     * Represents a segment of the breadcrumb path.
     * Each segment contains the page title and path.
     */
    public static class PathSegment {
        private final String name;
        private final String path;

        /**
         * Constructs a new PathSegment with the specified title and path.
         *
         * @param name The title of the page.
         * @param path The path of the page.
         */
        public PathSegment(String name, String path) {
            this.name = name;
            this.path = path;
        }

        /**
         * Returns the title of the page.
         *
         * @return The title of the page.
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the path of the page.
         *
         * @return The path of the page.
         */
        public String getPath() {
            return path;
        }
    }
}
