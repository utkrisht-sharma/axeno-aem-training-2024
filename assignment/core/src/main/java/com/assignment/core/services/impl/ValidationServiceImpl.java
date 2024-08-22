package com.assignment.core.services.impl;

import com.assignment.core.services.ValidationService;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Implementation of the Validation Service.
 * Provides validation methods for parameter fields.
 */
@Component(
        name = "Validation service",
        service = ValidationService.class,
        immediate = true
)
public class ValidationServiceImpl implements ValidationService {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationServiceImpl.class);

    private static final String NUMBER_REGEX = "^[+-]?([0-9]*[.])?[0-9]+$";  // Regex for valid numbers, including decimals
    private static final String INTEGER_REGEX = "^[+-]?\\d+$";              // Regex for valid integers

    /**
     * Validates the client name.
     *
     * @return {@code Optional} an error message if validation fails;
     *         {@code Optional.empty()} if valid.
     */
    @Override
    public Optional<String> validateClientName(String clientName) {
        return validateRequired(clientName, "Client name");
    }

    /**
     * Validates the client income.
     *
     */
    @Override
    public Optional<String> validateClientIncome(String clientIncome) {
        return validateNumber(clientIncome, "Client income");
    }

    /**
     * Validates the loan amount.
     *
     */
    @Override
    public Optional<String> validateLoanAmount(String loanAmount) {
        return validateNumber(loanAmount, "Loan amount");
    }

    /**
     * Validates the loan term.
     *
     */
    @Override
    public Optional<String> validateLoanTerm(String loanTerm) {
        return validateInteger(loanTerm, "Loan term");
    }

    /**
     * Validates the existing EMIs.
     *
     */
    @Override
    public Optional<String> validateExistingEMIs(String existingEMIs) {
        return validateNumber(existingEMIs, "Existing EMIs");
    }

    /**
     * Validates the interest rate.
     *
     */
    @Override
    public Optional<String> validateInterestRate(String interestRate) {
        return validateNumber(interestRate, "Interest rate");
    }

    /**
     * Validates that a field value is not null or empty.
     *
     * @return {@code Optional} containing an error message if the value is null or empty;
     *         {@code Optional.empty()} if the value is valid.
     */
    private Optional<String> validateRequired(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return Optional.of(fieldName + " is required.");
        }
        return Optional.empty();
    }

    /**
     * Validates that a value matches the number format.
     *
     * @return {@code Optional} containing an error message if the value does not match the number format;
     *         {@code Optional.empty()} if the value is valid.
     */
    private Optional<String> validateNumber(String value, String fieldName) {
        Optional<String> error = validateRequired(value, fieldName);
        if (error.isPresent()) {
            return error;
        }
        if (!value.matches(NUMBER_REGEX)) {
            LOG.error("Number format error for {}: {}", fieldName, value);
            return Optional.of("Invalid " + fieldName.toLowerCase() + " format. Please enter a valid number.");
        }
        return Optional.empty();
    }

    /**
     * Validates that a value matches the integer format.
     *
     * @return {@code Optional} containing an error message if the value does not match the integer format;
     *         {@code Optional.empty()} if the value is valid.
     */
    private Optional<String> validateInteger(String value, String fieldName) {
        Optional<String> error = validateRequired(value, fieldName);
        if (error.isPresent()) {
            return error;
        }
        if (!value.matches(INTEGER_REGEX)) {
            LOG.error("Integer format error for {}: {}", fieldName, value);
            return Optional.of("Invalid " + fieldName.toLowerCase() + " format. Please enter a valid integer.");
        }
        return Optional.empty();
    }
}
