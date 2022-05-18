package com.azure.spring.initializr.extension.scm.push.github.restclient;

import com.azure.spring.initializr.extension.scm.push.common.service.GitService;
import com.azure.spring.initializr.extension.scm.push.common.service.GitServiceFactory;

public class GithubServiceFactory implements GitServiceFactory {

    private static final String GITHUB = "github";

    private GitHubOAuthClient gitHubOAuthClient;

    private GitHubClient gitHubClient;

    public GithubServiceFactory(GitHubOAuthClient gitHubOAuthClient, GitHubClient gitHubClient) {
        this.gitHubOAuthClient = gitHubOAuthClient;
        this.gitHubClient = gitHubClient;
    }

    @Override
    public GitService getGitService(String code) {
        return new GitService(gitHubOAuthClient, gitHubClient, code);
    }

    @Override
    public boolean support(String type) {
        return GITHUB.equals(type);
    }
}
