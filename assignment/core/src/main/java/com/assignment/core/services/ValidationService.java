package com.assignment.core.services;


import java.util.Map;

public interface ValidationService {
    /**
     * Validates the provided parameters.
     */
    boolean validateParameters(Map<String, String> parameters);
}
