package com.assignment.core.models;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The {@code BreadcrumbModel} class is a Sling Model that provides the logic for generating
 * a breadcrumb trail for the current page in an AEM site.
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BreadcrumbModel {

    /**
     * The current page from which the breadcrumb trail is derived.
     */
    @ScriptVariable
    private Page currentPage;

    /**
     * A list of breadcrumb items representing the breadcrumb trail.
     */
    private List<BreadcrumbItem> breadcrumbs;

    /**
     * Initializes the breadcrumb model by generating the breadcrumb trail
     * starting from the current page's parent and grandparent, if available.
     */

    @PostConstruct
    protected void init() {
        breadcrumbs = new ArrayList<>();

        // Add parent page at the beginning if it exists and is not the root content page
        Page parent = currentPage.getParent();
        if (parent != null && !"/content".equals(parent.getPath())) {
            breadcrumbs.add(0, new BreadcrumbItem(parent.getTitle(), parent.getPath()));

            // Add grandparent page at the beginning if it exists and is not the root content page
            Page grandparent = parent.getParent();
            if (grandparent != null && !"/content".equals(grandparent.getPath())) {
                breadcrumbs.add(0, new BreadcrumbItem(grandparent.getTitle(), grandparent.getPath()));
            }
        }

        // Add current page at the end
        breadcrumbs.add(new BreadcrumbItem(currentPage.getTitle(), currentPage.getPath()));
    }


    /**
     * Retrieves the list of breadcrumb items.
     *
     * @return A list of {@code BreadcrumbItem} objects representing the breadcrumb trail.
     */
    public List<BreadcrumbItem> getBreadcrumbs() {
        return breadcrumbs;
    }
}
