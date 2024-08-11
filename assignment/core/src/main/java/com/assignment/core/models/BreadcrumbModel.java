package com.assignment.core.models;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BreadcrumbModel {

    @Self
    private SlingHttpServletRequest request;

    @ScriptVariable
    private Page currentPage;

    private List<BreadcrumbItem> breadcrumbs;

    @PostConstruct
    protected void init() {
        breadcrumbs = new ArrayList<>();

        Page parent = currentPage.getParent();
        if (parent != null && !"/content".equals(parent.getPath())) {
            breadcrumbs.add(new BreadcrumbItem(parent.getTitle(), parent.getPath()));

            Page grandparent = parent.getParent();
            if (grandparent != null && !"/content".equals(grandparent.getPath())) {
                breadcrumbs.add(new BreadcrumbItem(grandparent.getTitle(), grandparent.getPath()));
            }
        }


        Collections.reverse(breadcrumbs);
        breadcrumbs.add(new BreadcrumbItem(currentPage.getTitle(), currentPage.getPath()));
    }

    public List<BreadcrumbItem> getBreadcrumbs() {
        return breadcrumbs;
    }

    public static class BreadcrumbItem {
        private final String title;
        private final String path;

        public BreadcrumbItem(String title, String path) {
            this.title = title;
            this.path = path;
        }

        public String getTitle() {
            return title;
        }

        public String getPath() {
            return path;
        }
    }
}