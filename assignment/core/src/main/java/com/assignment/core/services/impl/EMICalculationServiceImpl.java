package com.assignment.core.services.impl;

import com.assignment.core.services.EMICalculationService;
import org.osgi.service.component.annotations.Component;

/**
 * This service provides functionality to calculate the EMI
 * based on the loan amount, annual interest rate, and loan term.
 */
@Component(
        name = "EMI calculation service",
        service = EMICalculationService.class,
        immediate = true
)
public class EMICalculationServiceImpl implements EMICalculationService {

    /**
     * Calculates the EMI
     *
     * @param loanAmount    principal amount of the loan.
     * @param interestRate  annual interest rate (in percentage).
     * @param loanTerm      loan term in months.
     * @return The EMI amount to be paid monthly.
     */
    @Override
    public double calculateEMI(double loanAmount, double interestRate, int loanTerm) {
        double monthlyInterestRate = interestRate / 12 / 100;
        return (loanAmount * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanTerm)) /
                (Math.pow(1 + monthlyInterestRate, loanTerm) - 1);
    }
}

