package com.assignment.core.services.impl;

import com.assignment.core.models.RequestParameters;
import com.assignment.core.services.ValidationService;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = ValidationService.class)
public class ValidationServiceImpl implements ValidationService {

    private static final Logger log = LoggerFactory.getLogger(ValidationServiceImpl.class);
    String errorMessage;

    @Override
    public String validateSearchParameters(RequestParameters params) {
        log.info(" Validating parameters: " );

        if (StringUtils.isBlank(params.getSearchPath())) {
            errorMessage = "Search path is missing.";
            log.error(errorMessage);
            return errorMessage;
        }
        if (StringUtils.isBlank(params.getPropertyOne())) {
            errorMessage = "Property One is missing.";
            log.error(errorMessage);
            return errorMessage;
        }
        if (StringUtils.isBlank(params.getPropertyOneValue())) {
            errorMessage = "Property One Value is missing.";
            log.error(errorMessage);
            return errorMessage;
        }
        if (StringUtils.isBlank(params.getPropertyTwo())) {
            errorMessage = "Property Two is missing.";
            log.error(errorMessage);
            return errorMessage;
        }
        if (StringUtils.isBlank(params.getPropertyTwoValue())) {
            errorMessage = "Property Two Value is missing.";
            log.error(errorMessage);
            return errorMessage;
        }
        if (StringUtils.isBlank(params.getSaveParam())) {
            errorMessage = "Save parameter is missing.";
            log.error(errorMessage);
            return errorMessage;
        }

        log.info("Validation successful.." );
        return null;
    }
}

