package com.azure.spring.initializr.extension.scm.push.github.restclient;

import com.azure.spring.initializr.extension.scm.push.common.service.GitService;
import com.azure.spring.initializr.extension.scm.push.common.service.GitServiceFactory;
import com.azure.spring.initializr.metadata.scm.push.GitPush;

import static com.azure.spring.initializr.extension.scm.push.common.GitServiceEnum.GITHUB;

public class GithubServiceFactory implements GitServiceFactory {

    private final GitHubOAuthClient gitHubOAuthClient;

    private final GitHubClient gitHubClient;

    private final GitPush gitPush;

    public GithubServiceFactory(GitHubOAuthClient gitHubOAuthClient, GitHubClient gitHubClient, GitPush gitPush) {
        this.gitHubOAuthClient = gitHubOAuthClient;
        this.gitHubClient = gitHubClient;
        this.gitPush = gitPush;
    }

    @Override
    public GitService getGitService(String code) {
        return new GitService(gitHubOAuthClient, gitHubClient, code, gitPush);
    }

    @Override
    public boolean support(String gitServiceType) {
        return GITHUB.getName().equals(gitServiceType);
    }
}
