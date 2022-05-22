package com.azure.spring.initializr.extension.scm.push.github.restclient;

import com.azure.spring.initializr.extension.scm.push.common.service.GitService;
import com.azure.spring.initializr.extension.scm.push.common.service.GitServiceFactory;
import com.azure.spring.initializr.metadata.scm.push.GitPush;
import com.azure.spring.initializr.metadata.scm.push.OAuthApp;
import org.springframework.web.reactive.function.client.WebClient;

import static com.azure.spring.initializr.extension.scm.push.common.GitServiceEnum.GITHUB;

public class GithubServiceFactory implements GitServiceFactory {

    private final GitPush.Config gitPushConfig;
    private final GitHubOAuthClient gitHubOAuthClient;
    private final WebClient webClient;

    public GithubServiceFactory(GitPush.Config gitPushConfig, OAuthApp oAuthApp, WebClient webClient) {
        this.gitPushConfig = gitPushConfig;
        this.gitHubOAuthClient = new GitHubOAuthClient(oAuthApp, webClient);
        this.webClient = webClient;
    }

    @Override
    public GitService getGitService(String authorizationCode) {
        String accessToken = this.gitHubOAuthClient.getAccessToken(authorizationCode).getAccessToken();
        GitHubRestClient gitHubRestClient = new GitHubRestClient(accessToken, this.webClient);
        return new GitService(accessToken, gitHubRestClient, gitPushConfig);
    }

    @Override
    public boolean support(String gitServiceType) {
        return GITHUB.getName().equals(gitServiceType);
    }
}
