package com.assignment.core.services.impl;

import com.assignment.core.models.RequestParameters;
import com.assignment.core.services.ValidationService;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Implementation of the ValidationService for validating search parameters.
 */
@Component(
        name= "Validation service",
        service = ValidationService.class,
        immediate = true
)
public class ValidationServiceImpl implements ValidationService {

    private static final Logger log = LoggerFactory.getLogger(ValidationServiceImpl.class);

    /**
     * Validates the provided search parameters.
     *
     * @param params request parameters to validate.
     * @return An Optional containing an error message if validation fails, otherwise an empty Optional.
     */
    @Override
    public Optional<String> validateSearchParameters(RequestParameters params) {
        log.info("Validating parameters...");

        if (StringUtils.isBlank(params.getSearchPath())) {
            return Optional.of("Search path is missing.");
        }

        if (StringUtils.isBlank(params.getPropertyOne())) {
            return Optional.of("Property One is missing.");
        }

        if (StringUtils.isBlank(params.getPropertyOneValue())) {
            return Optional.of("Property One Value is missing.");
        }

        if (StringUtils.isBlank(params.getPropertyTwo())) {
            return Optional.of("Property Two is missing.");
        }

        if (StringUtils.isBlank(params.getPropertyTwoValue())) {
            return Optional.of("Property Two Value is missing.");
        }

        if (StringUtils.isBlank(params.getSaveParam())) {
            return Optional.of("Save parameter is missing.");
        }

        log.info("Validation successful.");
        return Optional.empty();
    }
}
