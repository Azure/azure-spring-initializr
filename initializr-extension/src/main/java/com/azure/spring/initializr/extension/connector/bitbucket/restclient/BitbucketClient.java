package com.azure.spring.initializr.extension.connector.bitbucket.restclient;

import com.azure.spring.initializr.extension.connector.common.exception.ConnectorException;
import com.azure.spring.initializr.extension.connector.common.model.User;
import com.azure.spring.initializr.extension.connector.common.model.CreateRepo;
import com.azure.spring.initializr.extension.connector.common.restclient.ConnectorClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class BitbucketClient implements ConnectorClient {
    private static Logger logger = LoggerFactory.getLogger(BitbucketClient.class);

    final static String BASE_URI = "https://api.bitbucket.org/2.0";
    final static String CREATE_REPO_PATH = "/repositories";
    final static String GET_USER = "/user";

    WebClient bitbucketClient = WebClient.builder()
            .baseUrl(BASE_URI)
            .build();

    /**
     * Get current login user info.
     *
     * @param accessToken
     * @return
     */
    @Override
    public User getUser(String accessToken) {
        Assert.notNull(accessToken, "Invalid accessToken");

        try {
            return bitbucketClient
                    .get()
                    .uri(GET_USER)
                    .header("Authorization", getToken(accessToken))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(User.class)
                    .block();
        } catch (RuntimeException ex) {
            logger.error("An error occurred while getting user info.", ex);
            throw new ConnectorException("An error occurred while getting user info.");
        }
    }

    @Override
    public String createRepo(String accessToken, CreateRepo repo) {
        Assert.notNull(accessToken, "Invalid accessToken");
        Assert.notNull(repo, "Invalid repo");

        try {
            return bitbucketClient
                    .post()
                    .uri(CREATE_REPO_PATH + "/" + repo.getWorkSpace() + "/" + repo.getName())
                    .header("Authorization", getToken(accessToken))
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class).block();
        } catch (RuntimeException ex) {
            logger.error("An error occurred while creating repo.", ex);
            throw new ConnectorException("An error occurred while creating repo.");
        }

    }

    @Override
    public boolean repositoryExists(String accessToken, String loginName, String repoName) {

        Assert.notNull(accessToken, "Invalid accessToken");
        Assert.notNull(loginName, "Invalid loginName");
        Assert.notNull(repoName, "Invalid repoName");
        try {
            HttpStatus httpStatus = bitbucketClient
                    .get()
                    .uri("/repositories/" + loginName + "/" + repoName)
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
            logger.error("An error occurred while checking repository status.", ex);
            throw new ConnectorException("An error occurred while checking repository status.");
        }
    }

    private String getToken(String accessToken) {
        return "Bearer " + accessToken;
    }

}
