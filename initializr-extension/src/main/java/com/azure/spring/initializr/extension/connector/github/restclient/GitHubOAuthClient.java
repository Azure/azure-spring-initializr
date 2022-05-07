package com.azure.spring.initializr.extension.connector.github.restclient;

import com.azure.spring.initializr.extension.connector.github.model.GHAccessToken;
import com.azure.spring.initializr.metadata.connector.Connector;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

/**
 * doc
 * - https://docs.github.com/en/developers/apps/building-oauth-apps/authorizing-oauth-apps
 * getAuthorize_Code
 * // getcode with uri
 * - https://github.com/login/oauth/authorize?redirect_uri=http://localhost:8080/login/oauth2/code/github&client_id=1b17c66d923a18bc63c0&scope=public_repo
 */
// used to get accessToken from github
@Component
public class GitHubOAuthClient {

    private Connector githubConnector;

    public GitHubOAuthClient(Connector githubConnector) {
        this.githubConnector = githubConnector;
    }

    final static String BASE_URI = "https://github.com";
    final static String ACCESS_TOKEN_URI = "/login/oauth/access_token";

    WebClient githubOauthClient = WebClient.builder()
            .baseUrl(BASE_URI)
            .build();

    public GHAccessToken getAccessToken(String code) {
        // @TODO check params
        Map map = new HashMap();
        map.put("client_id", githubConnector.getClientId());
        map.put("client_secret", githubConnector.getClientSecret());
        map.put("redirect_uri", githubConnector.getRedirectUri());
        map.put("code", code);
        return githubOauthClient
                .post()
                .uri(ACCESS_TOKEN_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(map))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(GHAccessToken.class)
                .block();
    }
}
