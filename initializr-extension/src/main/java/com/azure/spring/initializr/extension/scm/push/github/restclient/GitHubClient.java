package com.azure.spring.initializr.extension.scm.push.github.restclient;

import com.azure.spring.initializr.extension.scm.push.common.exception.OAuthAppException;
import com.azure.spring.initializr.extension.scm.push.common.model.User;
import com.azure.spring.initializr.extension.scm.push.common.restclient.GitClient;
import com.azure.spring.initializr.web.project.ExtendProjectRequest;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

public class GitHubClient implements GitClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubClient.class);

    private static final String BASE_URI = "https://api.github.com";
    private static final String CREATE_REPO_PATH = "/user/repos";
    private static final String GET_USER = "/user";

    private final WebClient.Builder builder;

    public GitHubClient(WebClient.Builder builder) {
        this.builder = builder;
    }

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
            throw new OAuthAppException("An error occurred while getting user info.");
        }

    }

    @Override
    public String createRepository(String accessToken, User user, ExtendProjectRequest request) {
        Assert.notNull(accessToken, "Invalid accessToken.");
        Assert.notNull(request, "Invalid request.");
        Assert.notNull(user, "Invalid user.");

        try {
            Map<String, String> map = new HashMap<>();
            map.put("name", request.getArtifactId());

            CreatedRepository createdRepository = builder.baseUrl(BASE_URI)
                    .build()
                    .post()
                    .uri(CREATE_REPO_PATH)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(map))
                    .header("Authorization", getToken(accessToken))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(CreatedRepository.class).block();
            return createdRepository.getRepositoryUrl();
        } catch (RuntimeException ex) {
            LOGGER.error("An error occurred while creating repo.", ex);
            throw new OAuthAppException("An error occurred while creating repo.");
        }
    }

    @Override
    public boolean repositoryExists(String accessToken, User user, ExtendProjectRequest request) {
        Assert.notNull(accessToken, "Invalid accessToken.");
        Assert.notNull(user, "Invalid user.");
        Assert.notNull(request, "Invalid request.");
        try {
            HttpStatus httpStatus = builder
                    .baseUrl(BASE_URI).build()
                    .get()
                    .uri("/repos/" + user.getUsername() + "/" + request.getArtifactId())
                    .header("Authorization", getToken(accessToken))
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .block()
                    .statusCode();
            return "OK".equals(httpStatus.getReasonPhrase());
        } catch (RuntimeException ex) {
            LOGGER.error("An error occurred while checking repository status.", ex);
            throw new OAuthAppException("An error occurred while checking repository status.");
        }
    }

    private String getToken(String accessToken) {
        return "token " + accessToken;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CreatedRepository {

        @JsonAlias("html_url")
        private String repositoryUrl;

        public String getRepositoryUrl() {
            return repositoryUrl;
        }

        public void setRepositoryUrl(String repositoryUrl) {
            this.repositoryUrl = repositoryUrl;
        }
    }

}
