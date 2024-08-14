package com.assignment.core.services;

/**
 * Creating Service interface for validating request parameters.
 */
public interface ParameterValidationService {

    /**
     * Validates a string parameter to ensure it is not null or empty.
     *
     * @param paramName the name of the parameter (for error messages).
     * @param value     the value of the parameter to validate.
     * @return true if the parameter is valid; false otherwise.
     */
    boolean validateString(String paramName, String value);

    /**
     * Validates a numeric parameter to ensure it is not null and can be parsed to the correct type.
     *
     * @param paramName the name of the parameter (for error messages).
     * @param value     the value of the parameter to validate.
     * @return true if the parameter is valid; false otherwise.
     */
    boolean validateDouble(String paramName, String value);

    /**
     * Validates an integer parameter to ensure it is not null and can be parsed to the correct type.
     *
     * @param paramName the name of the parameter (for error messages).
     * @param value     the value of the parameter to validate.
     * @return true if the parameter is valid; false otherwise.
     */
    boolean validateInteger(String paramName, String value);
}
