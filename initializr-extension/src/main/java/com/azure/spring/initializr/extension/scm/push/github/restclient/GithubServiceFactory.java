package com.azure.spring.initializr.extension.scm.push.github.restclient;

import com.azure.spring.initializr.extension.scm.push.common.service.GitService;
import com.azure.spring.initializr.extension.scm.push.common.service.GitServiceFactory;
import com.azure.spring.initializr.metadata.scm.push.GitPush;

import static com.azure.spring.initializr.extension.scm.push.common.GitServiceEnum.GITHUB;

public class GithubServiceFactory implements GitServiceFactory {

    private final GitPush gitPush;

    private final GitHubClient gitHubClient;

    private final GitHubOAuthClient gitHubOAuthClient;

    public GithubServiceFactory(
            GitHubOAuthClient gitHubOAuthClient, GitHubClient gitHubClient, GitPush gitPush) {
        this.gitHubOAuthClient = gitHubOAuthClient;
        this.gitHubClient = gitHubClient;
        this.gitPush = gitPush;
    }

    @Override
    public GitService getGitService(String authorizationCode) {
        return new GitService(gitHubOAuthClient, gitHubClient, authorizationCode, gitPush);
    }

    @Override
    public boolean support(String gitServiceType) {
        return GITHUB.getName().equals(gitServiceType);
    }
}
