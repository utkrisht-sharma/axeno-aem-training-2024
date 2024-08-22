package com.assignment.core.services;


import java.util.Optional;

/**
 * Service interface for validating various client and loan-related fields.
 * Provides methods to validate client name, income, loan amount, loan term,
 * existing EMIs, and interest rate.
 */
public interface ValidationService {


    Optional<String> validateClientName(String clientName);
    Optional<String> validateClientIncome(String clientIncome);
    Optional<String> validateLoanAmount(String loanAmount);
    Optional<String> validateLoanTerm(String loanTerm);
    Optional<String> validateExistingEMIs(String existingEMIs);
    Optional<String> validateInterestRate(String interestRate);
}
