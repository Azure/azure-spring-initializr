package com.azure.spring.initializr.extension.scm.push.common.restclient;

import com.azure.spring.initializr.extension.scm.push.common.model.User;
import com.azure.spring.initializr.web.project.ExtendProjectRequest;

public interface GitClient {

    User getUser(String accessToken);

    String createRepository(String accessToken, User user, ExtendProjectRequest request) ;

    boolean repositoryExists(String accessToken, User user, ExtendProjectRequest request);

}
