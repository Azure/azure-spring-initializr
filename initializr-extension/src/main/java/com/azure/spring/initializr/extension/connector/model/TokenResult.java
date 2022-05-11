package com.azure.spring.initializr.extension.connector.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class TokenResult {
    @JsonAlias("access_token")
    String accessToken;

    String scope;

    @JsonAlias("token_type")
    String tokenType;

    String error;

    @JsonAlias("error_description")
    String errorDescription;

    @JsonAlias("error_uri")
    String errorUri;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getErrorUri() {
        return errorUri;
    }

    public void setErrorUri(String errorUri) {
        this.errorUri = errorUri;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
