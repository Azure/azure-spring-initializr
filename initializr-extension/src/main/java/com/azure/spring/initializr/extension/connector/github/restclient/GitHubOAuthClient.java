package com.azure.spring.initializr.extension.connector.github.restclient;

import com.azure.spring.initializr.extension.connector.exception.ConnectorException;
import com.azure.spring.initializr.extension.connector.model.TokenResult;
import com.azure.spring.initializr.extension.connector.restclient.OAuthClient;
import com.azure.spring.initializr.metadata.connector.Connector;
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
@ConditionalOnProperty(prefix = "initializr.connectors", name = "github.enabled", havingValue = "true")
public class GitHubOAuthClient implements OAuthClient {
    private final static Logger logger = LoggerFactory.getLogger(GitHubOAuthClient.class);

    final static String BASE_URI = "https://github.com";
    final static String ACCESS_TOKEN_URI = "/login/oauth/access_token";

    private final Connector connector;

    public GitHubOAuthClient(Connector connector) {
        this.connector = connector;
    }

    private WebClient githubOauthClient = WebClient.builder()
            .baseUrl(BASE_URI)
            .build();

    @Override
    public TokenResult getAccessToken(String authorizationCode) {
        Assert.notNull(authorizationCode, "Invalid authorizationCode.");

        try {
            Map map = new HashMap();
            map.put("client_id", connector.getClientId());
            map.put("client_secret", connector.getClientSecret());
            map.put("redirect_uri", connector.getRedirectUri());
            map.put("code", authorizationCode);
            return githubOauthClient
                    .post()
                    .uri(ACCESS_TOKEN_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(map))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(TokenResult.class)
                    .block();
        } catch (RuntimeException ex) {
            logger.error("Error while authenticating", ex);
            throw new ConnectorException("Error while authenticating");
        }

    }

}
