package com.azure.spring.initializr.extension.scm.github.restclient;

import com.azure.spring.initializr.extension.scm.common.exception.SCMException;
import com.azure.spring.initializr.extension.scm.common.model.Repository;
import com.azure.spring.initializr.extension.scm.common.model.User;
import com.azure.spring.initializr.extension.scm.common.restclient.GitClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class GitHubClient implements GitClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubClient.class);

    private final static String BASE_URI = "https://api.github.com";
    private final static String CREATE_REPO_PATH = "/user/repos";
    private final static String GET_USER = "/user";

    private final WebClient.Builder builder;

    public GitHubClient(WebClient.Builder builder) {
        this.builder = builder;
    }

    /**
     * Get current login user info.
     *
     * @param accessToken
     * @return
     */
    @Override
    public User getUser(String accessToken) {
        Assert.notNull(accessToken, "Invalid accessToken.");
        try {
            WebClient.Builder builder = WebClient.builder();
            return builder.baseUrl(BASE_URI)
                    .build()
                    .get()
                    .uri(GET_USER)
                    .header("Authorization", getToken(accessToken))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block();
        } catch (RuntimeException ex) {
            LOGGER.error("An error occurred while getting user info.", ex);
            throw new SCMException("An error occurred while getting user info.");
        }

    }

    @Override
    public String createRepository(String accessToken, Repository repository) {
        Assert.notNull(accessToken, "Invalid accessToken");
        Assert.notNull(repository, "Invalid repo");

        try {
            return builder.baseUrl(BASE_URI)
                    .build()
                    .post()
                    .uri(CREATE_REPO_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(repository))
                    .header("Authorization", getToken(accessToken))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class).block();
        } catch (RuntimeException ex) {
            LOGGER.error("An error occurred while creating repo.", ex);
            throw new SCMException("An error occurred while creating repo.");
        }

    }

    @Override
    public boolean repositoryExists(String accessToken, String loginName, String repoName) {
        Assert.notNull(accessToken, "Invalid accessToken");
        Assert.notNull(loginName, "Invalid loginName");
        Assert.notNull(repoName, "Invalid repoName");
        try {
            HttpStatus httpStatus = builder
                    .baseUrl(BASE_URI).build()
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
        } catch (RuntimeException ex) {
            LOGGER.error("An error occurred while checking repository status.", ex);
            throw new SCMException("An error occurred while checking repository status.");
        }
    }

    private String getToken(String accessToken) {
        return "token " + accessToken;
    }

}
