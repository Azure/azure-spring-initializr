package com.azure.spring.initializr.extension.scm.common.service;

public interface GitServiceFactory {

    GitService getGitService(String code);

    boolean support(String type);

}
