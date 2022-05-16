package com.azure.spring.initializr.extension.connector.common;

import com.azure.spring.initializr.extension.connector.common.exception.ConnectorException;
import com.azure.spring.initializr.extension.connector.github.restclient.GitHubOAuthClient;
import com.azure.spring.initializr.extension.connector.common.model.GitRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.RemoteAddCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class GitRepositoryService {
    private static Logger logger = LoggerFactory.getLogger(GitHubOAuthClient.class);

    /**
     * pushToGithub
     *
     * @throws GitAPIException
     * @throws URISyntaxException
     */
    public static void pushToGitRepository(GitRepository gitRepository) {
        try {
            Assert.notNull(gitRepository.getToken(), "Invalid token.");
            Assert.notNull(gitRepository.getOwnerName(), "Invalid owner name.");
            gitRepository.setEmail("SpringIntegSupport@microsoft.com");
            //01. Init a git repo.
            Git repo = Git.init()
                    .setInitialBranch(gitRepository.getInitialBranch())
                    .setDirectory(gitRepository.getTemplateFile())
                    .call();
            //02. Add all files to git repo.
            repo.add().addFilepattern(".").call();
            runCommand(gitRepository.getTemplateFile().toPath(), "git","add","*.md","-f");
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
        } catch (GitAPIException gitAPIException) {
            logger.error("An error occurred while pushing to the git repo.", gitAPIException);
            throw new ConnectorException("An error occurred while pushing to the git repo.");
        } catch (URISyntaxException uriSyntaxException) {
            logger.error("An error occurred while setting gituri of the git repo.", uriSyntaxException);
            throw new ConnectorException("An error occurred while setting gituri of the git repo.");
        } catch (IOException ioException) {
            logger.error("An IO error occurred while initializing the git repo.", ioException);
            throw new ConnectorException("An IO error occurred while initializing the git repo.");
        } catch (InterruptedException interruptedException) {
            logger.error("An error occurred while initializing the git repo.", interruptedException);
            throw new ConnectorException("An error occurred while initializing the git repo.");
        }
    }

    private static void runCommand(Path directory, String... command) throws IOException, InterruptedException {
        Objects.requireNonNull(directory, "directory");
        if (!Files.exists(directory)) {
            throw new RuntimeException("can't run command in non-existing directory '" + directory + "'");
        }
        ProcessBuilder pb = new ProcessBuilder()
                .command(command)
                .directory(directory.toFile());
        Process p = pb.start();
        StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");
        StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
        outputGobbler.start();
        errorGobbler.start();
        int exit = p.waitFor();
        errorGobbler.join();
        outputGobbler.join();
        if (exit != 0) {
            throw new AssertionError(String.format("runCommand returned %d", exit));
        }
    }

    private static class StreamGobbler extends Thread {

        private final InputStream is;
        private final String type;

        private StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        @Override
        public void run() {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is));) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(type + "> " + line);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
