package com.assignment.core.listeners;


import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.observation.ResourceChange;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Listens for resource changes in the specified paths and logs the changes.
 */
@Component(immediate = true,
        service = ResourceChangeListener.class,
        property = {ResourceChangeListener.PATHS + "=/content/assignment",
                ResourceChangeListener.CHANGES + "=REMOVED",
                ResourceChangeListener.CHANGES + "=CHANGED",
                ResourceChangeListener.CHANGES + "=ADDED"

        })
public class EventListener implements ResourceChangeListener {

    private static final Logger log = LoggerFactory.getLogger(EventListener.class);

    /**
     * Handles resource change events and logs details about the changes.
     */
    @Override
    public void onChange(List<ResourceChange> list) {
        log.info("Change Detected.");
        for (ResourceChange change : list) {
            log.info("Chane Occurs At = {}, Change Is = {}", change.getPath(), change.getType());
        }

    }
}
