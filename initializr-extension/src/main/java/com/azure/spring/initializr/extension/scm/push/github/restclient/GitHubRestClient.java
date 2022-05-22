package com.azure.spring.initializr.extension.scm.push.github.restclient;

import com.azure.spring.initializr.extension.scm.push.common.exception.OAuthAppException;
import com.azure.spring.initializr.extension.scm.push.common.model.User;
import com.azure.spring.initializr.extension.scm.push.common.client.GitRestClient;
import com.azure.spring.initializr.web.project.ExtendProjectRequest;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GitHubRestClient implements GitRestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GitHubRestClient.class);
    private static final String API_BASE_URI = "https://api.github.com";
    private static final String CREATE_REPO_PATH = API_BASE_URI + "/user/repos";
    private static final String GET_USER_PATH = API_BASE_URI + "/user";
    private static final String REPO_EXISTS_PATTERN = API_BASE_URI + "/repos/%s/%s";

    private final WebClient webClient;
    private final String accessToken;

    public GitHubRestClient(String accessToken, WebClient webClient) {
        this.accessToken = accessToken;
        this.webClient = webClient;
    }

    @Override
    public User getUser() {
        try {
            return webClient
                    .get()
                    .uri(GET_USER_PATH)
                    .header("Authorization", getToken())
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
    public String createRepository(User user, ExtendProjectRequest request) {
        Assert.notNull(request, "Invalid request.");
        Assert.notNull(user, "Invalid user.");
        try {
            Map<String, String> map = new HashMap<>();
            map.put("name", request.getArtifactId());

            CreatedRepository createdRepository =
                    webClient
                        .post()
                        .uri(CREATE_REPO_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue(map))
                        .header("Authorization", getToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .bodyToMono(CreatedRepository.class)
                        .block();
            Assert.notNull(createdRepository, "Invalid createdRepository.");
            return createdRepository.getRepositoryUrl();
        } catch (RuntimeException ex) {
            LOGGER.error("An error occurred while creating repo.", ex);
            throw new OAuthAppException("An error occurred while creating repo.");
        }
    }

    @Override
    public boolean repositoryExists(User user, ExtendProjectRequest request) {
        Assert.notNull(user, "Invalid user.");
        Assert.notNull(request, "Invalid request.");
        try {
            HttpStatus httpStatus =
                    Objects.requireNonNull(webClient
                                    .get()
                                    .uri(String.format(
                                            REPO_EXISTS_PATTERN,
                                            user.getUsername(),
                                            request.getArtifactId()))
                                    .header("Authorization", getToken())
                                    .accept(MediaType.APPLICATION_JSON)
                                    .exchangeToMono(Mono::just)
                                    .flatMap(ClientResponse::toBodilessEntity)
                                    .block())
                            .getStatusCode();
            return "OK".equals(httpStatus.getReasonPhrase());
        } catch (RuntimeException ex) {
            LOGGER.error("An error occurred while checking repository status.", ex);
            throw new OAuthAppException("An error occurred while checking repository status.");
        }
    }

    private String getToken() {
        return "token " + this.accessToken;
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
