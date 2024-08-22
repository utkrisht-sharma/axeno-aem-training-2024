package com.assignment.core.services;



/**
 * ValidationService is an interface that defines the contract for validating parameters
 * in a given context. Implementations of this interface are responsible for providing
 * validation logic for the specified parameters and returning appropriate error messages
 * if any parameters are invalid.
 */
public interface ValidationService {

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
    String validateParameters(String path, String propertyOne, String propertyOneValue,
                              String propertyTwo, String propertyTwoValue);
}
