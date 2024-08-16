package com.assignment.core.services.impl;

import com.assignment.core.services.ValidationService;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@Component(service = ValidationService.class)
public class ValidationServiceImpl implements ValidationService {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationServiceImpl.class);


    /**
     * Validates the provided parameters.
     */
    @Override
    public boolean validateParameters(String path, String propertyOne, String propertyOneValue, String propertyTwo, String propertyTwoValue,String save) {
        if(!Objects.nonNull(path) || path.trim().isEmpty()){
            LOG.error("Validation Fail : Invalid search path");
            return false;
        }
        if(!Objects.nonNull(propertyOne) || propertyOne.trim().isEmpty()){
            LOG.error("Validation Fail : Invalid Property One");
            return false;
        }
        if(!Objects.nonNull(propertyOneValue) || propertyOneValue.trim().isEmpty()){
            LOG.error("Validation Fail : Invalid Property One Value");
            return false;
        }
        if(!Objects.nonNull(propertyTwo) || propertyTwo.trim().isEmpty()){
            LOG.error("Validation Fail : Invalid Property Two");
            return false;
        }
        if(!Objects.nonNull(propertyTwoValue) || propertyTwoValue.trim().isEmpty()){
            LOG.error("Validation Fail : Invalid Property Two Value");
            return false;
        }
        if(!Objects.nonNull(save) || save.trim().isEmpty() || !(save.equalsIgnoreCase("true") || save.equalsIgnoreCase("false"))){
            LOG.error("Validation Fail : Invalid Property Save");
            return false;
        }

        LOG.info("Validation Successfully Completed");
        return true;
    }
}
