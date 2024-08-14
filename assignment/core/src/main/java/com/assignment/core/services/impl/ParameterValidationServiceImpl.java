package com.assignment.core.services.impl;

import com.assignment.core.services.ParameterValidationService;
import org.osgi.service.component.annotations.Component;

/**
 * Implementation of the ParameterValidationService interface.
 * This service provides methods to validate request parameters requested by user.
 */
@Component(service = ParameterValidationService.class)
public class ParameterValidationServiceImpl implements ParameterValidationService {

    @Override
    public boolean validateString(String paramName, String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return true;
    }

    @Override
    public boolean validateDouble(String paramName, String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {

            return false;
        }
    }

    @Override
    public boolean validateInteger(String paramName, String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
