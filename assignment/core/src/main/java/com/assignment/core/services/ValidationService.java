package com.assignment.core.services;

public interface ValidationService {


    String validateClientName(String clientName);
    String validateClientIncome(String clientIncome);
    String validateLoanAmount(String loanAmount);
    String validateLoanTerm(String loanTerm);
    String validateExistingEMIs(String existingEMIs);
    String validateInterestRate(String interestRate);
}
