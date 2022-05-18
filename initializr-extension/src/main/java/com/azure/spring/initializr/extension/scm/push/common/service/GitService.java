package com.azure.spring.initializr.extension.scm.push.common.service;


import com.azure.spring.initializr.extension.scm.push.common.exception.OAuthAppException;
import com.azure.spring.initializr.extension.scm.push.common.model.Repository;
import com.azure.spring.initializr.extension.scm.push.common.model.TokenResult;
import com.azure.spring.initializr.extension.scm.push.common.model.User;
import com.azure.spring.initializr.extension.scm.push.common.restclient.GitClient;
import com.azure.spring.initializr.extension.scm.push.common.restclient.OAuthClient;
import com.azure.spring.initializr.web.scm.push.PushToGitProjectRequest;
import io.spring.initializr.web.project.ProjectGenerationResult;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.net.URISyntaxException;

public class GitService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitService.class);
    private static final String GIT_INIT_EMAIL = "SpringIntegSupport@microsoft.com";
    private static final String GIT_INIT_MESSAGE = "Initial commit from Azure Spring Initializr";
    private static final String GIT_INIT_BRANCH = "main";

    private String code;

    private String accessToken;

    private boolean authorized = false;

    private GitClient gitClient;

    private OAuthClient oAuthClient;

    public GitService(OAuthClient oAuthClient, GitClient gitClient, String code) {
        this.gitClient = gitClient;
        this.oAuthClient = oAuthClient;
        this.code = code;
        getAccessToken();
    }

    public String getCode() {
        return code;
    }

    public String pushToGitRepository(PushToGitProjectRequest request, ProjectGenerationResult result) {
        checkParameters(request);

        if (request.getBaseDir() == null) {
            request.setBaseDir(request.getName());
        }

        User user = getUser();
        String username = user.getUsername();
        String artifactId = request.getArtifactId();
        boolean repositoryExists = repositoryExists(username, artifactId);

        if (repositoryExists) {
            throw new OAuthAppException("There is already a project named ' "
                    + artifactId
                    + "' on your " + request.getGitServiceType()
                    + ", please retry with a different name (the artifact is the name)...");
        }

        Repository repository = new Repository();
        repository.setName(artifactId);
        repository.setWorkSpace(username);
        createRepository(repository);

        String gitRepositoryUrl = "https://github.com/" + username + "/" + artifactId;
        File path = new File(result.getRootDirectory().toFile().getAbsolutePath()
                + "/" + request.getBaseDir());
        gitPush(username, path, gitRepositoryUrl);
        return gitRepositoryUrl;
    }

    private String getAccessToken() {
        if (!authorized) {
            TokenResult tokenResult = oAuthClient.getAccessToken(code);
            authorized = true;
            if (StringUtils.isNotEmpty(tokenResult.getAccessToken())) {
                accessToken = tokenResult.getAccessToken();
            }else {
                throw new OAuthAppException(tokenResult.getError());
            }
        }
        return accessToken;
    }

    private User getUser() {
        return gitClient.getUser(accessToken);
    }

    private String createRepository(Repository repository) {
        return gitClient.createRepository(accessToken, repository);
    }

    private boolean repositoryExists(String username, String repoName) {
        return gitClient.repositoryExists(accessToken, username, repoName);
    }

    /**
     */
    private void gitPush(String userName, File directory, String  gitRepoUrl) {
        try {
            Assert.notNull(accessToken, "Invalid token.");
            Assert.notNull(userName, "Invalid userName name.");

            Git repo = commit(userName, directory);
            gitPush(accessToken, userName, gitRepoUrl, repo);
            clean(repo);
        } catch (GitAPIException gitAPIException) {
            LOGGER.error("An error occurred while pushing to the git repo.", gitAPIException);
            throw new OAuthAppException("An error occurred while pushing to the git repo.");
        } catch (URISyntaxException uriSyntaxException) {
            LOGGER.error("An error occurred while setting gituri of the git repo.", uriSyntaxException);
            throw new OAuthAppException("An error occurred while setting gituri of the git repo.");
        }
    }

    private void clean(Git repo) throws GitAPIException {
        repo.clean().call();
        repo.close();
    }

    private Git commit(String userName, File directory) throws GitAPIException {
        Git repo = Git.init()
                .setInitialBranch(GIT_INIT_BRANCH)
                .setDirectory(directory)
                .call();
        repo.add().addFilepattern(".").call();
        repo.commit().setMessage(GIT_INIT_MESSAGE)
                .setAuthor(userName, GIT_INIT_EMAIL)
                .setCommitter(userName, GIT_INIT_EMAIL)
                .setSign(false)
                .call();
        return repo;
    }

    private void gitPush(String token, String userName, String gitRepoUrl, Git repo) throws GitAPIException, URISyntaxException {
        RemoteAddCommand remote = repo.remoteAdd();
        remote.setName("origin")
                .setUri(new URIish(gitRepoUrl)).call();
        PushCommand pushCommand = repo.push();
        pushCommand.add("HEAD");
        pushCommand.setRemote("origin");
        pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(userName,
                token));
        pushCommand.call();
    }

    private void checkParameters(PushToGitProjectRequest request) {
        Assert.notNull(request.getArtifactId(), "Invalid request param artifactId.");
        Assert.notNull(request.getCode(), "Invalid request param code.");
        Assert.notNull(request.getName(), "Invalid request param name.");
        Assert.notNull(request.getType(), "Invalid request param type.");
        Assert.notNull(request.getLanguage(), "Invalid request param language.");
        Assert.notNull(request.getArchitecture(), "Request: param architecture.");
        Assert.notNull(request.getPackaging(), "Invalid request param packaging.");
        Assert.notNull(request.getGroupId(), "Invalid request param groupId.");
        Assert.notNull(request.getArtifactId(), "Invalid request param artifactId.");
        Assert.notNull(request.getDescription(), "Invalid request param description.");
        Assert.notNull(request.getPackageName(), "Invalid request param packageName.");
        Assert.notNull(request.getBootVersion(), "Invalid request param bootVersion.");
        Assert.notNull(request.getJavaVersion(), "Invalid request param javaVersion.");
    }


}
