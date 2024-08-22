package com.assignment.core.services;

/**
 * Service interface for determining loan eligibility.
 * Provides a method to check if a client is eligible for a loan based on their income,
 * loan amount, loan term, existing EMIs, and interest rate.
 */
public interface LoanEligibilityService {

    boolean isEligible(double clientIncome, double loanAmount, int loanTerm, double existingEMIs, double interestRate);
}
