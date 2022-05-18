package com.azure.spring.initializr.extension.scm.push.common.restclient;

import com.azure.spring.initializr.extension.scm.push.common.model.TokenResult;

public interface OAuthClient {
    TokenResult getAccessToken(String code);
}

