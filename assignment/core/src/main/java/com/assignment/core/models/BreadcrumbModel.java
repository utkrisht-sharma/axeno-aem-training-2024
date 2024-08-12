package com.assignment.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.*;
import org.apache.sling.models.annotations.injectorspecific.*;

import java.util.*;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BreadcrumbModel {

    @SlingObject
    private ResourceResolver resolver;

    @Self
    private Resource currentResource;

    @ValueMapValue
    private String activePage;

    public List<PathSegment> getNavigationPath() {
        List<PathSegment> segments = new LinkedList<>();

        Resource pageResource = resolver.getResource(activePage);
        if (pageResource == null) {
            return segments;
        }

        Resource parentResource = pageResource.getParent();
        int depth = 0;

        while (parentResource != null && depth < 2) {
            segments.add(0, new PathSegment(parentResource.getName(), parentResource.getPath()));
            parentResource = parentResource.getParent();
            depth++;
        }

        return segments;
    }

    public static class PathSegment {
        private final String name;
        private final String path;

        public PathSegment(String name, String url) {
            this.name = name;
            this.path = url;
        }

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }
    }
}