package com.azure.spring.initializr.extension.connector.bitbucket.restclient;

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
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 *
 */
@Component
@ConditionalOnProperty(prefix = "initializr.connectors", name = "bitbucket.enabled", havingValue = "true")
public class BitbucketOAuthClient implements OAuthClient {
    private static final Logger logger = LoggerFactory.getLogger(BitbucketOAuthClient.class);

    final static String BASE_URI = "https://bitbucket.org";
    final static String ACCESS_TOKEN_URI = "/site/oauth2/access_token";

    private final Connector connector;

    public BitbucketOAuthClient(Connector connector) {
        this.connector = connector;
    }

    private WebClient githubOauthClient = WebClient.builder()
            .baseUrl(BASE_URI)
            .build();

    @Override
    public TokenResult getAccessToken(String authorizationCode) {
        Assert.notNull(authorizationCode, "Invalid authorizationCode.");

        try {
            return githubOauthClient
                    .post()
                    .uri(ACCESS_TOKEN_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Basic " + Base64Utils
                            .encodeToString((connector.getClientId()
                                             + ":"
                                             + connector.getClientSecret()).getBytes(UTF_8)))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(BodyInserters.fromFormData("code", authorizationCode)
                            .with("grant_type", "authorization_code"))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .flatMap(clientResponse -> {
                        if (clientResponse.statusCode().is5xxServerError()
                                || clientResponse.statusCode().is4xxClientError()) {
                            clientResponse.body((clientHttpResponse, context) -> {
                                return clientHttpResponse.getBody();
                            });
                            return clientResponse.bodyToMono(TokenResult.class);
                        } else
                            return clientResponse.bodyToMono(TokenResult.class);
                    }).block();
        } catch (RuntimeException ex) {
            logger.error("Error while authenticating", ex);
            throw new ConnectorException("Error while authenticating");
        }
    }
}
