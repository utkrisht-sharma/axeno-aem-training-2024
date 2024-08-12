package com.assignment.core.models;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.*;
import org.apache.sling.models.annotations.injectorspecific.*;

import java.awt.desktop.ScreenSleepEvent;
import java.util.*;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BreadcrumbModel {

    @SlingObject
    private ResourceResolver resolver;

    @Self
    private Resource currentResource;

    public List<PathSegment> getNavigationPath() {
        List<PathSegment> segments = new LinkedList<>();

        Resource pageResource = currentResource;

        if (pageResource == null) {
            return segments;
        }

        Resource currentPage = getCurrentPage(pageResource);

        Resource parentResource = currentPage.getParent();
        int depth = 0;

        while (parentResource != null && depth < 3) {
            segments.add(0, new PathSegment(parentResource.getName(), parentResource.getPath()));
            parentResource = parentResource.getParent();
            depth++;
        }

        return segments;
    }

    private Resource getCurrentPage(Resource currentResource){
        while( !isPage(currentResource)){
            currentResource= currentResource.getParent();
        }
        return currentResource;
    }

    private boolean isPage(Resource currentResource){
        return "cq:Page".equals(currentResource.getValueMap().get("jcr:primaryType"));
    }

    public static class PathSegment {
        private final String name;
        private final String path;

        public PathSegment(String name, String path) {
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
}