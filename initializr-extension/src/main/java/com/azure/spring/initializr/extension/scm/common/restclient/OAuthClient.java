package com.azure.spring.initializr.extension.scm.common.restclient;

import com.azure.spring.initializr.extension.scm.common.model.TokenResult;

public interface OAuthClient {
    TokenResult getAccessToken(String code);
}

