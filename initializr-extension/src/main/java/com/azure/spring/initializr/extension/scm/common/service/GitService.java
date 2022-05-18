package com.azure.spring.initializr.extension.scm.common.service;


import com.azure.spring.initializr.extension.scm.common.exception.OAuthAppException;
import com.azure.spring.initializr.extension.scm.common.model.GitRepository;
import com.azure.spring.initializr.extension.scm.common.model.Repository;
import com.azure.spring.initializr.extension.scm.common.model.User;
import com.azure.spring.initializr.extension.scm.common.restclient.GitClient;
import com.azure.spring.initializr.extension.scm.common.restclient.OAuthClient;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.*;
import java.net.URISyntaxException;

public class GitService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitService.class);

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

    public String getAccessToken() {
        if (!authorized) {
            accessToken = oAuthClient.getAccessToken(code).getAccessToken();
            authorized = true;
        }
        return accessToken;
    }

    public User getUser() {
        return gitClient.getUser(accessToken);
    }

    public String createRepository(Repository repository) {
        return gitClient.createRepository(accessToken, repository);
    }

    public boolean repositoryExists(String username, String repoName) {
        return gitClient.repositoryExists(accessToken, username, repoName);
    }

    public String getCode() {
        return code;
    }

    /**
     * pushToGithub
     *
     */
    public static void pushToGitRepository(GitRepository gitRepository) {
        try {
            Assert.notNull(gitRepository.getToken(), "Invalid token.");
            Assert.notNull(gitRepository.getUserName(), "Invalid owner name.");
            gitRepository.setEmail("SpringIntegSupport@microsoft.com");
            // remove HELP.md in .gitignore
            removeLineInGitignore("HELP.md", gitRepository.getTemplateFile().getAbsolutePath());
            //01. Init a git repo.
            Git repo = Git.init()
                    .setInitialBranch(gitRepository.getInitialBranch())
                    .setDirectory(gitRepository.getTemplateFile())
                    .call();
            //02. Add all files to git repo.
            repo.add().addFilepattern(".").call();
            //03. Add all files to git repo.
            repo.commit().setMessage("Initial commit from Azure Spring Initializr")
                    .setAuthor(gitRepository.getUserName(), gitRepository.getEmail())
                    .setCommitter(gitRepository.getUserName(), gitRepository.getEmail())
                    .setSign(false)
                    .call();
            //04. Add remote
            RemoteAddCommand remote = repo.remoteAdd();
            remote.setName("origin")
                    .setUri(new URIish(gitRepository.getHttpTransportUrl())).call();
            //05. Push to remote
            PushCommand pushCommand = repo.push();
            pushCommand.add("HEAD");
            pushCommand.setRemote("origin");
            pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitRepository.getUserName(),
                    gitRepository.getToken()));
            pushCommand.call();
        } catch (GitAPIException gitAPIException) {
            LOGGER.error("An error occurred while pushing to the git repo.", gitAPIException);
            throw new OAuthAppException("An error occurred while pushing to the git repo.");
        } catch (URISyntaxException uriSyntaxException) {
            LOGGER.error("An error occurred while setting gituri of the git repo.", uriSyntaxException);
            throw new OAuthAppException("An error occurred while setting gituri of the git repo.");
        } catch (IOException ioException) {
            LOGGER.error("An IO error occurred while initializing the git repo.", ioException);
            throw new OAuthAppException("An IO error occurred while initializing the git repo.");
        }
    }

    private static void removeLineInGitignore(String lineToRemove, String path) throws IOException {
        File inputFile = new File(path + File.separator + ".gitignore");
        File tempFile = new File(path + File.separator + "temp");

        BufferedReader reader = new BufferedReader(new FileReader(inputFile));
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

        String currentLine;

        while((currentLine = reader.readLine()) != null) {
            // trim newline when comparing with lineToRemove
            String trimmedLine = currentLine.trim();
            if(trimmedLine.equals(lineToRemove)) continue;
            writer.write(currentLine + System.getProperty("line.separator"));
        }
        writer.close();
        reader.close();
        tempFile.renameTo(inputFile);
    }


}