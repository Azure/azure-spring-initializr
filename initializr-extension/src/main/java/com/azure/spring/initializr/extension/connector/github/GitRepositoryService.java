package com.azure.spring.initializr.extension.connector.github;

import com.azure.spring.initializr.extension.connector.model.GitRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.util.Assert;

import java.net.URISyntaxException;

public class GitRepositoryService {
    /**
     * pushToGithub
     * @throws GitAPIException
     * @throws URISyntaxException
     */
    public static void pushToGitRepository(GitRepository gitRepository) throws GitAPIException, URISyntaxException {
        Assert.notNull(gitRepository.getToken(), "Invalid token.");
        Assert.notNull(gitRepository.getOwnerName(), "Invalid owner name.");
        Assert.notNull(gitRepository.getEmail(), "Invalid email address.");

        // @TODO Catch Exception
        //-------------------
        //01. Init a git repo.
        Git repo = Git.init()
                      .setInitialBranch(gitRepository.getInitialBranch())
                      .setDirectory(gitRepository.getTemplateFile())
                      .call();
        //02. Add all files to git repo.
        repo.add().addFilepattern(".").call();
        //03. Add all files to git repo.
        repo.commit().setMessage("Initial commit from Azure Spring Initializr")
                     .setAuthor(gitRepository.getOwnerName(), gitRepository.getEmail())
                     .setCommitter(gitRepository.getOwnerName(), gitRepository.getEmail())
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
        pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(gitRepository.getOwnerName(),
                                                                                   gitRepository.getToken()));
        pushCommand.call();
    }
}
