package com.assignment.core.services;

public interface OAuthService {
    String getAccessToken(String code);
}