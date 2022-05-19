package com.azure.spring.initializr.extension.scm.push.common.restclient;

import com.azure.spring.initializr.extension.scm.push.common.model.TokenResult;

public interface OAuthClient {
    /**
     * Get accessToken to access remote Git service through OAuth2 authorization code flow.
     *
     * @param code authorization Code
     * @return
     */
    TokenResult getAccessToken(String code);
}
