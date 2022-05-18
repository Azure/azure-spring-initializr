package com.azure.spring.initializr.extension.scm.push.common.service;


import com.azure.spring.initializr.extension.scm.push.common.exception.OAuthAppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;
import java.util.stream.Collectors;

public class GitServiceFactoryDelegate {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitServiceFactoryDelegate.class);

    private final List<GitServiceFactory> gitServiceFactories;

    public GitServiceFactoryDelegate(ObjectProvider<GitServiceFactory> gitServiceFactories) {
        this.gitServiceFactories = gitServiceFactories.orderedStream().collect(Collectors.toList());
    }

    public GitService getGitService(String gitServiceType, String code) {
        if (gitServiceFactories == null) {
            LOGGER.error("No GitServiceFactory is available.");
            throw new OAuthAppException("An error occurred while initializing git service.");
        }
        for (GitServiceFactory gitServiceFactory : gitServiceFactories) {
            if (gitServiceFactory.support(gitServiceType)) {
                return gitServiceFactory.getGitService(code);
            }
        }
        LOGGER.error("No suitable GitServiceFactory for {}.", gitServiceType);
        throw new OAuthAppException("An error occurred while getting git service.");
    }

}
