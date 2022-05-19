package com.azure.spring.initializr.extension.scm.push.common.restclient;

import com.azure.spring.initializr.extension.scm.push.common.model.User;
import com.azure.spring.initializr.web.project.ExtendProjectRequest;

public interface GitClient {

    /**
     * Get userinfo by accessToken.
     *
     * @param accessToken accessToken to access remote Git service
     * @return current login user info
     */
    User getUser(String accessToken);

    /**
     * Create repository with specified name in remote Git service repositories.
     *
     * @param accessToken accessToken to access remote Git service
     * @param user        current login user
     * @param request     user request info
     * @return
     */
    String createRepository(String accessToken, User user, ExtendProjectRequest request);

    /**
     * Check whether repository with specified name exists in remote Git service repositories.
     *
     * @param accessToken accessToken to access remote Git service
     * @param user        current login user
     * @param request     user request info
     * @return
     */
    boolean repositoryExists(String accessToken, User user, ExtendProjectRequest request);
}
