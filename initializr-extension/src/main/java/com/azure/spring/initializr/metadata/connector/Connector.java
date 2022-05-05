package com.azure.spring.initializr.metadata.connector;

public class Connector {
    String type;
    String clientId;
    String oauthUri;

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
}
