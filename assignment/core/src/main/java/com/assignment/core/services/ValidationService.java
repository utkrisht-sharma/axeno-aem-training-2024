package com.assignment.core.services;

import com.assignment.core.models.RequestParameters;

import java.util.Optional;

/**
 * Service interface for validation.
 */
public interface ValidationService {

    /**
     * Validates the provided request parameters.
     *
     */
    Optional<String> validateSearchParameters(RequestParameters params);
}
