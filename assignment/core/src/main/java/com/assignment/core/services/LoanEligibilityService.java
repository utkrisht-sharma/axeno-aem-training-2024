package com.assignment.core.services;

public interface LoanEligibilityService {
    boolean isEligible(double clientIncome, double loanAmount, int loanTerm, double existingEMIs, double interestRate);
}

