package com.azure.spring.initializr.extension.connector.common.restclient;

import com.azure.spring.initializr.extension.connector.common.model.CreateRepo;
import com.azure.spring.initializr.extension.connector.common.model.User;

public interface ConnectorClient {

    User getUser(String accessToken);

    String  createRepo(String accessToken, CreateRepo repo) ;

    boolean repositoryExists(String accessToken, String ownerName, String repoName);

}
