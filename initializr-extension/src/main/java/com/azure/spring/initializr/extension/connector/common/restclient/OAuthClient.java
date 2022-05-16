package com.azure.spring.initializr.extension.connector.common.restclient;

import com.azure.spring.initializr.extension.connector.common.model.TokenResult;

public interface OAuthClient {
    TokenResult getAccessToken(String code);
}

