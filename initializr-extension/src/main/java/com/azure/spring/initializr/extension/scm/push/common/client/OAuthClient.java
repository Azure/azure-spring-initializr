package com.azure.spring.initializr.extension.scm.push.common.client;

import com.azure.spring.initializr.extension.scm.push.common.model.TokenResult;
import org.springframework.lang.NonNull;

public interface OAuthClient {
    /**
     * Get accessToken to access remote Git service through OAuth2 authorization code flow.
     *
     * @param code authorization Code
     * @return {@link com.azure.spring.initializr.extension.scm.push.common.model.TokenResult}
     */
    @NonNull
    TokenResult getAccessToken(String code);
}
