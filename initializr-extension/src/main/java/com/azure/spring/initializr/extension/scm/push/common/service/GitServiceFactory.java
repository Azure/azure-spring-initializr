package com.azure.spring.initializr.extension.scm.push.common.service;

public interface GitServiceFactory {

    GitService getGitService(String code);

    boolean support(String gitServiceType);

}
