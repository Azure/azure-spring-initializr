package com.azure.spring.initializr.extension.connector.restclient;

import com.azure.spring.initializr.extension.connector.model.CreateRepo;
import com.azure.spring.initializr.extension.connector.github.model.GitHubUser;
import org.springframework.http.HttpStatus;

public interface ConnectorClient {
    GitHubUser getUser(String accessToken);


    String  createRepo(String accessToken, CreateRepo repo) ;

    //check repo exists
    HttpStatus repositoryExists(String accessToken, String ownerName, String repoName);

}
