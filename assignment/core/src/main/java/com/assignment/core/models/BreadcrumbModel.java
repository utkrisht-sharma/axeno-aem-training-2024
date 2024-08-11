
package com.assignment.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class BreadcrumbModel {

    @SlingObject
    private Resource resource;

    private List<BreadcrumbItem> items;

    @PostConstruct
    public void init() {
        items = getItems();
    }

    public List<BreadcrumbItem> getItems() {
        List<BreadcrumbItem> items = new ArrayList<>();
        Resource currentPage = getCurrentPage(resource);
        Resource parentPage = currentPage != null ? currentPage.getParent() : null;
        Resource grandparentPage = parentPage != null ? parentPage.getParent() : null;

        if (grandparentPage != null) {
            processPage(grandparentPage, items);
        }
        if (parentPage != null ) {
            processPage(parentPage, items);
        }
        if (currentPage != null) {
            processPage(currentPage, items);
        }

        return items;
    }

    private Resource getCurrentPage(Resource resource) {
        while (resource != null && !isPage(resource)) {
            resource = resource.getParent();
        }
        return resource;
    }

    private void processPage(Resource page, List<BreadcrumbItem> items) {
        String title = page.getValueMap().get("jcr:title", page.getName());
        String url = getPageUrl(page);
        boolean active = page.equals(getCurrentPage(resource));
        BreadcrumbItem item = new BreadcrumbItem(title, url, active);
        items.add(item);
    }

    private boolean isPage(Resource resource) {
        return resource != null && "cq:Page".equals(resource.getValueMap().get("jcr:primaryType"));
    }


    private String getPageUrl(Resource page) {
        ResourceResolver resolver = page.getResourceResolver();
        String path = page.getPath();

        return resolver.map(path) +".html" ;
    }

}
