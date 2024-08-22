package com.assignment.core.services;

/**
 * Service interface for triggering jobs within the system.
 * This interface defines methods to trigger jobs with specified properties.
 * Implementations of this interface should provide the logic for job execution
 * as required by the application.
 */
public interface JobTriggerService {

    /**
     * Triggers the periodic logger job.
     * This method is responsible for initiating the periodic logger job,
     * which logs messages at specified intervals. Implementations should
     * handle the scheduling and execution of the logger job.
     */
    void triggerPeriodicLoggerJob();
}
