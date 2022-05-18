package com.azure.spring.initializr.extension.scm.common.restclient;

import com.azure.spring.initializr.extension.scm.common.model.Repository;
import com.azure.spring.initializr.extension.scm.common.model.User;

public interface GitClient {

    User getUser(String accessToken);

    String createRepository(String accessToken, Repository repository) ;

    boolean repositoryExists(String accessToken, String username, String repoName);

}
