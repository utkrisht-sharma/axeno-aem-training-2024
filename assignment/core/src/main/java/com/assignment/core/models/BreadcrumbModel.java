package com.assignment.core.models;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BreadcrumbModel {

    @Self
    private Resource resource;

    @SlingObject
    private ResourceResolver resourceResolver;

    private List<BreadcrumbItem> items;

    @PostConstruct
    public void init() {
        items = getItems();
    }

    public List<BreadcrumbItem> getItems() {
        List<BreadcrumbItem> items = new ArrayList<>();
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

        if (pageManager != null) {
            Page currentPage = pageManager.getContainingPage(resource);
            if (currentPage != null) {
                Page parentPage = currentPage.getParent();
                Page grandparentPage = parentPage != null ? parentPage.getParent() : null;

                if (grandparentPage != null) {
                    processPage(grandparentPage, items);
                }
                if (parentPage != null) {
                    processPage(parentPage, items);
                }
                processPage(currentPage, items);
            }
        }

        return items;
    }

    private void processPage(Page page, List<BreadcrumbItem> items) {
        String title = page.getTitle() != null ? page.getTitle() : page.getName();
        String url = getPageUrl(page);
        boolean active = page.getPath().equals(resource.getPath());
        BreadcrumbItem item = new BreadcrumbItem(title, url, active);
        items.add(item);
    }

    private String getPageUrl(Page page) {
        return resourceResolver.map(page.getPath()) + ".html";
    }
}
