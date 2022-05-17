package com.azure.spring.initializr.extension.scm.common.service;


import org.springframework.beans.factory.ObjectProvider;

import java.util.List;

public class GitServiceFactoryDelegate {

    List<GitServiceFactory> gitServiceFactories;

    public GitServiceFactoryDelegate(ObjectProvider<List<GitServiceFactory>> gitServiceFactories) {
        this.gitServiceFactories = gitServiceFactories.getIfAvailable();
    }

    public GitService getGitService(String type, String code) {
        for (GitServiceFactory providerFactory : gitServiceFactories) {
            if (providerFactory.getType().equals(type)) {
                return providerFactory.getGitService(code);
            }
        }
        return null;
    }

    public void setGitServiceFactories(List<GitServiceFactory> gitServiceFactories) {
        this.gitServiceFactories = gitServiceFactories;
    }
}
