package com.azure.spring.initializr.extension.scm.push.common.service;

public interface GitServiceFactory {

    GitService getGitService(String authorizationCode);

    boolean support(String gitServiceType);
}
