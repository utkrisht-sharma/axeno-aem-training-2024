package com.assignment.core.services.impl;

import com.assignment.core.services.EMICalculationService;
import org.osgi.service.component.annotations.Component;

@Component(
        service = EMICalculationService.class
)
public class EMICalculationServiceImpl implements EMICalculationService {

    @Override
    public double calculateEMI(double loanAmount, double interestRate, int loanTerm) {
        double monthlyInterestRate = interestRate / 12 / 100;
        return (loanAmount * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanTerm)) /
                (Math.pow(1 + monthlyInterestRate, loanTerm) - 1);
    }
}
