package com.assignment.core.services;

public interface EMICalculationService {
    double calculateEMI(double loanAmount, double interestRate, int loanTerm);
}
