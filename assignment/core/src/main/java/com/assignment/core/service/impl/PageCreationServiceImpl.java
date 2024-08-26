package com.assignment.core.service.impl;

import com.assignment.core.service.PageCreationService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import java.util.Objects;
import java.util.UUID;


@Component(service = PageCreationService.class)
public class PageCreationServiceImpl implements PageCreationService {
    private static final Logger log = LoggerFactory.getLogger(PageCreationServiceImpl.class);

    /**
     * Creates a new page with the provided details.
     */
    @Override
    public void createPage(String path, String title, String description, String tags, String thumbnail, ResourceResolver resolver) throws PersistenceException, WCMException {
        log.info("Creation Of Page Started");
        PageManager pageManager = resolver.adaptTo(PageManager.class);
        if (!Objects.isNull(pageManager)) {
            log.info("PageManager Is Not Null. ");
            String uniquePageName = "page-" + UUID.randomUUID().toString();
            String templatePath = "/conf/assignment/settings/wcm/templates/homeloantemplate";
            Page newPage = pageManager.create(path, uniquePageName, templatePath, title);
            if (!Objects.isNull(newPage)) {
                log.info("New Page Is Created Successfully At Path {}. ", path);
                Resource pageContentResource = newPage.getContentResource();
                if (!Objects.isNull(pageContentResource)) {
                    log.info("Setting Page Properties ");
                    ModifiableValueMap properties = pageContentResource.adaptTo(ModifiableValueMap.class);
                    if (!Objects.isNull(properties)) {
                        properties.put("jcr:description", description);
                        properties.put("cq:tags", tags);
                        properties.put("cq:thumbnail", thumbnail);
                        log.info("Page properties set successfully.");

                    } else {
                        log.error("Failed to adapt resource to ModifiableValueMap.");
                        throw new PersistenceException("Could not adapt content resource to ModifiableMap.");

                    }

                } else {
                    log.error("Page Content Resource Is Null. ");
                    throw new PersistenceException("Content Resource Is Null.");
                }
            } else {
                log.error("New Page Is Null. ");
                throw new WCMException("Page creation failed for path: " + path);
            }
        } else {
            log.error("Page Manager Is Null. ");
            throw new PersistenceException("Page Manager Could Not Adapt From Resource. ");
        }

        log.info("{} Page Created Successfully. ", title);

    }
}
