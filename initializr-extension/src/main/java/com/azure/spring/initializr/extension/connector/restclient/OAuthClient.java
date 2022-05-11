package com.azure.spring.initializr.extension.connector.restclient;

import com.azure.spring.initializr.extension.connector.model.TokenResult;

public interface OAuthClient {
    TokenResult getAccessToken(String code);
}

