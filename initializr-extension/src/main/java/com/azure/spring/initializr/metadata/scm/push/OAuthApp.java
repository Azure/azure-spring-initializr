package com.azure.spring.initializr.metadata.scm.push;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class OAuthApp {
    private String clientId;

    private String oauthUri;

    private boolean enabled;

    @JsonIgnore
    private String clientSecret;

    private String redirectUri;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
