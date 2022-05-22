package com.azure.spring.initializr.extension.scm.push.common.service;

import com.azure.spring.initializr.extension.scm.push.common.GitServiceEnum;
import com.azure.spring.initializr.extension.scm.push.common.exception.OAuthAppException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;
import java.util.stream.Collectors;

public class GitServiceFactoryResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitServiceFactoryResolver.class);

    private final List<GitServiceFactory> gitServiceFactories;

    public GitServiceFactoryResolver(ObjectProvider<GitServiceFactory> gitServiceFactories) {
        this.gitServiceFactories = gitServiceFactories.orderedStream().collect(Collectors.toList());
    }

    /**
     * Get GitServiceFactory by gitServiceType
     *
     * @param gitServiceType  GitServiceType, check {@link GitServiceEnum} for all types.
     * @return GitServiceFactory
     *
     */
    public GitServiceFactory resolve(String gitServiceType) {
        for (GitServiceFactory gitServiceFactory : gitServiceFactories) {
            if (gitServiceFactory.support(gitServiceType)) {
                return gitServiceFactory;
            }
        }
        LOGGER.error("No suitable GitServiceFactory for {}.", gitServiceType);
        throw new OAuthAppException("An error occurred while getting git service.");
    }
}
