package com.assignment.core.services.impl;

import com.assignment.core.services.ValidationService;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component(service = ValidationService.class)
public class ValidationServiceImpl implements ValidationService {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationServiceImpl.class);


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean validateParameters(Map<String, String> parameters) {
        for (String fields : parameters.keySet()) {
            if (!Objects.nonNull(parameters.get(fields)) || parameters.get(fields).trim().isEmpty()) {
                LOG.error("Validation Fail : Invalid {} ", fields);
                return false;
            }

        }

        LOG.info("Validation Successfully Completed");
        return true;
    }
}
