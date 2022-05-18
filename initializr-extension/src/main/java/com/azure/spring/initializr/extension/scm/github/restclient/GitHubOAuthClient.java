package com.azure.spring.initializr.extension.scm.github.restclient;

import com.azure.spring.initializr.extension.scm.common.exception.OAuthAppException;
import com.azure.spring.initializr.extension.scm.common.model.TokenResult;
import com.azure.spring.initializr.extension.scm.common.restclient.OAuthClient;
import com.azure.spring.initializr.metadata.scm.OAuthApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

/**
 */
@Component
@ConditionalOnProperty(prefix = "initializr.scm.oauthapp", name = "github.enabled", havingValue = "true")
public class GitHubOAuthClient implements OAuthClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubOAuthClient.class);

    private final static String BASE_URI = "https://github.com";
    private final static String ACCESS_TOKEN_URI = "/login/oauth/access_token";

    private final OAuthApp oAuthApp;
    private final WebClient.Builder builder;


    public GitHubOAuthClient(OAuthApp oAuthApp, WebClient.Builder builder) {
        this.oAuthApp = oAuthApp;
        this.builder = builder;
    }

    @Override
    public TokenResult getAccessToken(String authorizationCode) {
        Assert.notNull(authorizationCode, "Invalid authorizationCode.");

        try {
            Map<String, String> map = new HashMap();
            map.put("client_id", oAuthApp.getClientId());
            map.put("client_secret", oAuthApp.getClientSecret());
            map.put("redirect_uri", oAuthApp.getRedirectUri());
            map.put("code", authorizationCode);
            return builder
                    .baseUrl(BASE_URI)
                    .build()
                    .post()
                    .uri(ACCESS_TOKEN_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(map))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(TokenResult.class)
                    .block();
        } catch (RuntimeException ex) {
            LOGGER.error("Error while authenticating", ex);
            throw new OAuthAppException("Error while authenticating");
        }
    }

}