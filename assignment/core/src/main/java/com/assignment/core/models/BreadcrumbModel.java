package com.assignment.core.models;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;


@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BreadcrumbModel {

    @Self
    public Resource resource;

    public List<Breadcrumb> breadcrumbs;

    @PostConstruct
    public void init() {
        breadcrumbs = new ArrayList<>();
        Breadcrumbs(resource, 0);
    }

    public void Breadcrumbs(Resource currentResource, int level) {
        if (currentResource == null || level > 2) {
            return;
        }
        String name = getResourceName(currentResource);
        String path = currentResource.getPath();
        breadcrumbs.add(new Breadcrumb(name, path));
        Resource parent = currentResource.getParent();
        if (parent != null) {
            Breadcrumbs(parent, level + 1);
        }
    }

    public String getResourceName(Resource resource) {
        if (resource == null) {
            return "";
        }
        return resource.getName();
    }


    public List<Breadcrumb> getBreadcrumbs() {
        return breadcrumbs;
    }
}