package com.azure.spring.initializr.extension.connector.github.restclient;

import com.azure.spring.initializr.extension.connector.model.CreateRepo;
import com.azure.spring.initializr.extension.connector.github.model.GitHubUser;
import com.azure.spring.initializr.extension.connector.restclient.ConnectorClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GitHubClient implements ConnectorClient {
    final static String BASE_URI = "https://api.github.com";
    final static String CREATE_REPO_PATH = "/user/repos";
    final static String GET_USER = "/user";

    WebClient githubClient = WebClient.builder()
            .baseUrl(BASE_URI)
            .build();

    /**
     * Get current login user info.
     *
     * @param accessToken
     * @return
     */
    @Override
    public GitHubUser getUser(String accessToken) {
        // @TODO check params
        // @TODO try catch
        return githubClient
                .get()
                .uri(GET_USER)
                .header("Authorization", getToken(accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(GitHubUser.class)
                .block();
    }

    @Override
    public String createRepo(String accessToken, CreateRepo repo) {

        // @TODO check params
        // @TODO try catch
        return githubClient
                .post()
                .uri(CREATE_REPO_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(repo))
                .header("Authorization", getToken(accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class).block();
    }

    @Override
    public boolean repositoryExists(String accessToken, String loginName, String repoName) {
        // @TODO check params
        // @TODO try catch
        HttpStatus httpStatus = githubClient
                .get()
                .uri("/repos/" + loginName + "/" + repoName)
                .header("Authorization", getToken(accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .block()
                .statusCode();
        if ("OK".equals(httpStatus.getReasonPhrase())) {
            return true;
        }
        return false;
    }

    private String getToken(String accessToken) {
        return "token " + accessToken;
    }

}
