package com.azure.spring.initializr.web.controller;

import com.azure.spring.initializr.extension.connector.github.GitHubService;
import com.azure.spring.initializr.extension.connector.github.model.GHAccessToken;
import com.azure.spring.initializr.extension.connector.github.model.GHCreateRepo;
import com.azure.spring.initializr.extension.connector.github.model.GitHubUser;
import com.azure.spring.initializr.web.connector.ConnectorProjectRequest;
import com.azure.spring.initializr.web.project.ExtendProjectRequest;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.controller.ProjectGenerationController;
import io.spring.initializr.web.project.ProjectGenerationInvoker;
import io.spring.initializr.web.project.ProjectGenerationResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.azure.spring.initializr.extension.connector.github.restclient.GitHubClient;
import com.azure.spring.initializr.extension.connector.github.restclient.GitHubOAuthClient;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Map;

public class ExtendProjectGenerationController extends ProjectGenerationController<ExtendProjectRequest> {
    ProjectGenerationInvoker<ExtendProjectRequest> projectGenerationInvoker;

    public ExtendProjectGenerationController(InitializrMetadataProvider metadataProvider,
                                             ProjectGenerationInvoker<ExtendProjectRequest> projectGenerationInvoker) {
        super(metadataProvider, projectGenerationInvoker);
        this.projectGenerationInvoker = projectGenerationInvoker;
    }

    @Autowired
    GitHubOAuthClient gitHubOAuthClient;

    @Autowired
    GitHubClient gitHubClient;

    @Override
    public ExtendProjectRequest projectRequest(Map<String, String> headers) {
        ExtendProjectRequest request = new ExtendProjectRequest();
        request.getParameters().putAll(headers);
        request.initialize(getMetadata());
        return request;
    }

    //@TODO add test case
    @RequestMapping(
            path = "/login/oauth2/code/github",
            produces = "application/json")
    @ResponseBody
    public Map connectGithub(ConnectorProjectRequest request) throws GitAPIException, URISyntaxException {
        //@TODO check param
        String artifactId = request.getArtifactId();
        String code = request.getCode();

        GHAccessToken ghaccessToken = gitHubOAuthClient.getAccessToken(code);
        String accessToken = ghaccessToken.getAccessToken();

        GitHubUser user = gitHubClient.getUser(accessToken);
        String login = user.getLogin();
        // check repositoryExists
        HttpStatus stringStatusCode = gitHubClient.repositoryExists(accessToken, login, artifactId);

        if ("OK".equals(stringStatusCode.getReasonPhrase())) {
            return Map.of("code", 201, "value", "repository already exists");
        }

        GHCreateRepo repo = new GHCreateRepo();
        repo.setName(artifactId);
        // create repository
        gitHubClient.createRepo(accessToken, repo);

        // Generate code
        // @TODO there is no help.md here.
        ProjectGenerationResult result = this.projectGenerationInvoker.invokeProjectStructureGeneration(request);
        // @TODO Generate others

        // push to Github
        Path rootDirectory = result.getRootDirectory();
        GitHubService gitHubService = new GitHubService();
        gitHubService.pushToGithub(artifactId,
                "main",
                login,
                new File(rootDirectory.toFile().getAbsolutePath() + "/" + request.getBaseDir()),
                accessToken);
        this.projectGenerationInvoker.cleanTempFiles(result.getRootDirectory());
        return Map.of("code", 200, "value", artifactId + " pushed successfully");
    }
}
