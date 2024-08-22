package com.assignment.core.listeners;

import org.apache.sling.api.SlingConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Event handler for content-related events.
 * This class listens for resource-related events such as addition, modification, and removal of resources.
 */
@Component(
        service = EventHandler.class,
        immediate = true,
        property = {
                EventConstants.EVENT_TOPIC + "=" + SlingConstants.TOPIC_RESOURCE_ADDED,
                EventConstants.EVENT_TOPIC + "=" + SlingConstants.TOPIC_RESOURCE_CHANGED,
                EventConstants.EVENT_TOPIC + "=" + SlingConstants.TOPIC_RESOURCE_REMOVED
        }
)
public class ContentEventListner implements EventHandler {

    private static final Logger log = LoggerFactory.getLogger(ContentEventListner.class);

    /**
     * Handles the received event.
     *
     * @param event the event to handle
     */
    @Override
    public void handleEvent(Event event) {
        String eventType = event.getTopic();
        String path = (String) event.getProperty(SlingConstants.PROPERTY_PATH);

        log.info("Event received: {}, Path: {}", eventType, path);
    }
}
