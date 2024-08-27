package com.assignment.core.services.impl;
import com.assignment.core.services.ValidationService;
import org.osgi.service.component.annotations.Component;

/**
 * Implementation of the {@link ValidationService} interface that provides
 * validation logic for parameters used in node management operations.
 * <p>
 * This service is registered as an OSGi component and can be used wherever
 * the {@link ValidationService} is required. It ensures that required parameters
 * are neither null nor empty, returning appropriate error messages when invalid
 * parameters are detected.
 * </p>
 */
@Component(service = ValidationService.class, immediate = true)
public class ValidationServiceImpl implements ValidationService {

    /**
     * Validates the required parameters and returns an error message if any are invalid.
     * <p>
     * This method checks each parameter individually and returns a specific error message
     * corresponding to the first invalid parameter encountered. If all parameters are valid,
     * the method returns {@code null}.
     * </p>
     *
     * @param path              The path parameter to validate.
     * @param propertyOne       The first property parameter to validate.
     * @param propertyOneValue  The value for the first property to validate.
     * @param propertyTwo       The second property parameter to validate.
     * @param propertyTwoValue  The value for the second property to validate.
     * @return A string containing the error message if any parameter is invalid, or {@code null} if all parameters are valid.
     */
    @Override
    public String validateParameters(String path, String propertyOne, String propertyOneValue,
                                     String propertyTwo, String propertyTwoValue) {
        if (isNullOrEmpty(path)) {
            return "Path parameter is required.";
        }
        if (isNullOrEmpty(propertyOne)) {
            return "PropertyOne parameter is required.";
        }
        if (isNullOrEmpty(propertyOneValue)) {
            return "PropertyOneValue parameter is required.";
        }
        if (isNullOrEmpty(propertyTwo)) {
            return "PropertyTwo parameter is required.";
        }
        if (isNullOrEmpty(propertyTwoValue)) {
            return "PropertyTwoValue parameter is required.";
        }
        return null; // All parameters are valid
    }

    /**
     * Checks if a string is null or empty.
     * <p>
     * This method is a utility function used internally within this class to validate strings.
     * It trims the input string to remove any leading or trailing whitespace before checking
     * for null or empty values.
     * </p>
     *
     * @param value The string to check.
     * @return {@code true} if the string is null or empty, {@code false} otherwise.
     */
    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
