package com.azure.spring.initializr.extension.scm.push.common.client;

import com.azure.spring.initializr.extension.scm.push.common.model.User;
import com.azure.spring.initializr.web.project.ExtendProjectRequest;

/**
 *
 */
public interface GitRestClient {

    /**
     * Get userinfo
     *
     * @return current login user info
     */
    User getUser();

    /**
     * Create repository with specified name in remote Git service repositories.
     *
     * @param user        current login user
     * @param request     user request info
     * @return            created remote git repository url
     */
    String createRepository(User user, ExtendProjectRequest request);

    /**
     * Check whether repository with specified name exists in remote Git service repositories.
     *
     * @param user        current login user
     * @param request     user request info
     * @return            true:  repository with specified name already exists
     *                    false: repository with specified name not exists
     */
    boolean repositoryExists(User user, ExtendProjectRequest request);

}
