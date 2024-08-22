package com.assignment.core.services;

/**
 * Service interface for calculating the Equated Monthly Installment (EMI).
 * Provides a method to compute the EMI based on the loan amount, interest rate, and loan term.
 */
public interface EMICalculationService {

    double calculateEMI(double loanAmount, double interestRate, int loanTerm);
}
