package com.azure.spring.initializr.extension.connector.github.restclient;

import com.azure.spring.initializr.extension.connector.github.model.GHCreateRepo;
import com.azure.spring.initializr.extension.connector.github.model.GitHubUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class GitHubClient {
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

    public String  createRepo(String accessToken, GHCreateRepo repo) {
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

    //check repo exists
    public HttpStatus repositoryExists(String accessToken, String ownerName, String repoName) {
        // @TODO check params
        // @TODO try catch
        return githubClient
                .get()
                .uri("/repos/"+ownerName+"/"+repoName)
                .header("Authorization", getToken(accessToken))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .block()
                .statusCode();
    }

    private String getToken(String authorizationCode) {
        return "token " + authorizationCode;
    }

}
