package com.azure.spring.initializr.web.controller;

import com.azure.spring.initializr.extension.connector.github.GitHubService;
import com.azure.spring.initializr.extension.connector.github.model.GHAccessToken;
import com.azure.spring.initializr.extension.connector.github.model.GHCreateRepo;
import com.azure.spring.initializr.extension.connector.github.model.GitHubUser;
import com.azure.spring.initializr.web.connector.ConnectorProjectRequest;
import com.azure.spring.initializr.web.connector.ResultCode;
import com.azure.spring.initializr.web.project.ExtendProjectRequest;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.controller.ProjectGenerationController;
import io.spring.initializr.web.project.ProjectGenerationInvoker;
import io.spring.initializr.web.project.ProjectGenerationResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import com.azure.spring.initializr.extension.connector.github.restclient.GitHubClient;
import com.azure.spring.initializr.extension.connector.github.restclient.GitHubOAuthClient;
import org.springframework.web.util.UriComponentsBuilder;

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

    @RequestMapping(path = "/login/oauth2/code/github", produces = "application/json")
    public String connectGithub(ConnectorProjectRequest request) throws GitAPIException, URISyntaxException {
        validateParameters(request);

        String code = request.getCode();
        String artifactId = request.getArtifactId();

        if (request.getBaseDir() == null & request.getName() != null) {
            request.setBaseDir(request.getName());
        }

        GHAccessToken ghaccessToken = gitHubOAuthClient.getAccessToken(code);
        String accessToken = ghaccessToken.getAccessToken();

        GitHubUser user = gitHubClient.getUser(accessToken);
        String login = user.getLogin();
        // check repositoryExists
        HttpStatus stringStatusCode = gitHubClient.repositoryExists(accessToken, login, artifactId);

        if ("OK".equals(stringStatusCode.getReasonPhrase())) {
            return redirectUriString(request,
                    ResultCode.CODE_REPO_ALREADY_EXISTS.getCode(),
                    "There is already a project named ' " + artifactId + "' on your GitHub, please retry with a different name (the artifact is the name)...");
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
        return redirectUriString(request, ResultCode.CODE_SUCCESS.getCode(), ResultCode.CODE_SUCCESS.getMsg());
    }

    private void validateParameters(ConnectorProjectRequest request) {
        Assert.notNull(request.getArtifactId(), "Invalide request param artifactId.");
        Assert.notNull(request.getCode(), "Invalide request param code.");
        Assert.notNull(request.getName(), "Invalide request param name.");
        Assert.notNull(request.getType(), "Invalide request param type.");
        Assert.notNull(request.getLanguage(), "Invalide request param language.");
        Assert.notNull(request.getArchitecture(), "Request: param architecture.");
        Assert.notNull(request.getPackaging(), "Invalide request param packaging.");
        Assert.notNull(request.getGroupId(), "Invalide request param groupId.");
        Assert.notNull(request.getArtifactId(), "Invalide request param artifactId.");
        Assert.notNull(request.getDescription(), "Invalide request param description.");
        Assert.notNull(request.getPackageName(), "Invalide request param packageName.");
        Assert.notNull(request.getBootVersion(), "Invalide request param bootVersion.");
        Assert.notNull(request.getJavaVersion(), "Invalide request param javaVersion.");
    }

    private String redirectUriString(ConnectorProjectRequest request, String errorCode, String msg) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
                .queryParam("errorcode", errorCode)
                .queryParam("msg", msg);

        if (request != null) {
            uriComponentsBuilder
                    .queryParam("name", request.getName())
                    .queryParam("type", request.getType())
                    .queryParam("language", request.getLanguage())
                    .queryParam("architecture", request.getArchitecture())
                    .queryParam("packaging", request.getPackaging())
                    .queryParam("groupId", request.getGroupId())
                    .queryParam("artifactId", request.getArtifactId())
                    .queryParam("description", request.getDescription())
                    .queryParam("packageName", request.getPackageName())
                    .queryParam("platformVersion", request.getBootVersion())
                    .queryParam("jvmVersion", request.getJavaVersion());
        }
        return "redirect:/#!" + uriComponentsBuilder.toUriString();
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public String invalidProjectRequest(IllegalArgumentException ex) {
        return redirectUriString(null, ResultCode.INVALID_PARAM.getCode(), ex.getMessage());
    }

}
