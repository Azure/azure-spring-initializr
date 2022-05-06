package com.azure.spring.initializr.metadata.connector;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Connector {
    private String type;
    private String clientId;
    private String oauthUri;

    @JsonIgnore
    private String clientSecret;

    private String redirectUri;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getOauthUri() {
        return oauthUri;
    }

    public void setOauthUri(String oauthUri) {
        this.oauthUri = oauthUri;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
