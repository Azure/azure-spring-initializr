package com.azure.spring.initializr.extension.scm.github.restclient;

import com.azure.spring.initializr.extension.scm.common.service.GitService;
import com.azure.spring.initializr.extension.scm.common.service.GitServiceFactory;

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
    public String getType() {
        return GITHUB;
    }
}