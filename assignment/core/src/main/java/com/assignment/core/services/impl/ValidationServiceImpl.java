package com.assignment.core.services.impl;

import com.assignment.core.services.ValidationService;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = ValidationService.class
)
public class ValidationServiceImpl implements ValidationService {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationServiceImpl.class);

    @Override
    public String validateClientName(String clientName) {
        String error = validateRequired(clientName, "Client name");
        if (error != null) {
            LOG.error("Validation failed for client name: {}", error);
        }
        return error;
    }

    @Override
    public String validateClientIncome(String clientIncome) {
        String error = validateNumber(clientIncome, "Client income");
        if (error != null) {
            LOG.error("Validation failed for client income: {}", error);
        }
        return error;
    }

    @Override
    public String validateLoanAmount(String loanAmount) {
        String error = validateNumber(loanAmount, "Loan amount");
        if (error != null) {
            LOG.error("Validation failed for loan amount: {}", error);
        }
        return error;
    }

    @Override
    public String validateLoanTerm(String loanTerm) {
        String error = validateInteger(loanTerm, "Loan term");
        if (error != null) {
            LOG.error("Validation failed for loan term: {}", error);
        }
        return error;
    }

    @Override
    public String validateExistingEMIs(String existingEMIs) {
        String error = validateNumber(existingEMIs, "Existing EMIs");
        if (error != null) {
            LOG.error("Validation failed for existing EMIs: {}", error);
        }
        return error;
    }

    @Override
    public String validateInterestRate(String interestRate) {
        String error = validateNumber(interestRate, "Interest rate");
        if (error != null) {
            LOG.error("Validation failed for interest rate: {}", error);
        }
        return error;
    }

    private String validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return fieldName + " is required.";
        }
        return null;
    }

    private String validateNumber(String value, String fieldName) {
        String error = validateRequired(value, fieldName);
        if (error != null) {
            return error;
        }
        try {

            Double.parseDouble(value);
        } catch (NumberFormatException e) {
            LOG.error("Number format error for {}: {}", fieldName, e.getMessage());
            return "Invalid " + fieldName.toLowerCase() + " format. Please enter a valid number.";
        }
        return null;
    }

    private String validateInteger(String value, String fieldName) {
        String error = validateRequired(value, fieldName);
        if (error != null) {
            return error;
        }
        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            LOG.error("Integer format error for {}: {}", fieldName, e.getMessage());
            return "Invalid " + fieldName.toLowerCase() + " format. Please enter a valid number.";
        }
        return null;
    }
}
