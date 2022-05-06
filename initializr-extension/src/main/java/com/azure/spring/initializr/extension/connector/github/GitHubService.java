package com.azure.spring.initializr.extension.connector.github;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.net.URISyntaxException;

public class GitHubService {
    /**
     * pushToGithub
     * @throws GitAPIException
     * @throws URISyntaxException
     */
    public void pushToGithub(String artifactId, String initialBranch, String ownerName, File templateFile, String token) throws GitAPIException, URISyntaxException {

        // @TODO Check Param
        // @TODO Catch Exception
        //-------------------
        String httpTransportUrl = "https://github.com/" + ownerName + "/" + artifactId;
        //01. Init a git repo.
        Git repo = Git.init().setInitialBranch(initialBranch)
                .setDirectory(templateFile)
                .call();
        //02. Add all files to git repo.
        repo.add().addFilepattern(".").call();
        //03. Add all files to git repo.
        repo.commit().setMessage("Initial commit")
                .setAuthor("Azure Spring Initializr", "no-reply@azure.com")
                .setCommitter("test", "no-reply@azure.com")
                .setSign(false)
                .call();
        //04. Add remote
        RemoteAddCommand remote = repo.remoteAdd();
        remote.setName("origin")
                .setUri(new URIish(httpTransportUrl)).call();
        //05. Push to remote
        PushCommand pushCommand = repo.push();
        pushCommand.add("HEAD");
        pushCommand.setRemote("origin");
        pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider(ownerName, token));
        pushCommand.call();
    }
}
