package com.assignment.core.services;


import com.assignment.core.models.RequestParameters;

public interface ValidationService {
    String validateSearchParameters(RequestParameters params);
}
