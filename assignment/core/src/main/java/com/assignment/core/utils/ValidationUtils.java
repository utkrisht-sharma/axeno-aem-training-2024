package com.assignment.core.utils;

/**
 * Utility class for validation of parameters.
 */
public class ValidationUtils {
    /**
     * Validates that the given parameter is not null and not empty.
     */
    public static String validateParam(String param) {
        if (param == null || param.isEmpty()) {
            throw new IllegalArgumentException("Missing or empty parameter");
        }
        return param;
    }
}
