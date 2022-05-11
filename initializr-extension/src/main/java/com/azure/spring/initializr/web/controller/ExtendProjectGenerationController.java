package com.azure.spring.initializr.web.controller;

import com.azure.spring.initializr.extension.connector.github.GitRepositoryService;
import com.azure.spring.initializr.extension.connector.model.CreateRepo;
import com.azure.spring.initializr.extension.connector.github.model.GitHubUser;
import com.azure.spring.initializr.extension.connector.model.TokenResult;
import com.azure.spring.initializr.extension.connector.model.GitRepository;
import com.azure.spring.initializr.web.connector.ConnectorProjectRequest;
import com.azure.spring.initializr.web.connector.ResultCode;
import com.azure.spring.initializr.web.project.ExtendProjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.*;

public class ExtendProjectGenerationController extends ProjectGenerationController<ExtendProjectRequest> {

    ProjectGenerationInvoker<ExtendProjectRequest> projectGenerationInvoker;

    public ExtendProjectGenerationController(InitializrMetadataProvider metadataProvider,
                                             ProjectGenerationInvoker<ExtendProjectRequest> projectGenerationInvoker) {
        super(metadataProvider, projectGenerationInvoker);
        this.projectGenerationInvoker = projectGenerationInvoker;
    }

    @Autowired(required = false)
    GitHubOAuthClient gitHubOAuthClient;

    @Autowired(required = false)
    GitHubClient gitHubClient;

    @Override
    public ExtendProjectRequest projectRequest(Map<String, String> headers) {
        ExtendProjectRequest request = new ExtendProjectRequest();
        request.getParameters().putAll(headers);
        request.initialize(getMetadata());
        return request;
    }

    @RequestMapping(path = "/login/oauth2/code/github")
    public String pushToGitRepository(ConnectorProjectRequest request) throws GitAPIException, URISyntaxException {
        if (gitHubClient == null || gitHubOAuthClient == null) {
            return redirectUriString(request,
                    ResultCode.CODE_404.getCode(),
                    ResultCode.CODE_404.getMsg());
        }

        checkParameters(request);

        String authorizationCode = request.getCode();
        String artifactId = request.getArtifactId();

        if (request.getBaseDir() == null & request.getName() != null) {
            request.setBaseDir(request.getName());
        }

        // get accessToken
        TokenResult tokenResult = gitHubOAuthClient.getAccessToken(authorizationCode);
        String accessToken = tokenResult.getAccessToken();
        if (accessToken == null) {
            return redirectUriString(request,
                    ResultCode.ACCESSTOKEN_EMPTY.getCode(),
                    tokenResult.getError());
        }

        GitHubUser user = gitHubClient.getUser(accessToken);
        String loginName = user.getLogin();
        // check repositoryExists
        HttpStatus stringStatusCode = gitHubClient.repositoryExists(accessToken, loginName, artifactId);

        if ("OK".equals(stringStatusCode.getReasonPhrase())) {
            return redirectUriString(request,
                    ResultCode.CODE_REPO_ALREADY_EXISTS.getCode(),
                    "There is already a project named ' "
                          + artifactId
                          + "' on your GitHub, please retry with a different name (the artifact is the name)...");
        }

        CreateRepo repo = new CreateRepo();
        repo.setName(artifactId);
        gitHubClient.createRepo(accessToken, repo);

        // Generate code
        // @TODO there is no help.md here.
        ProjectGenerationResult result = this.projectGenerationInvoker.invokeProjectStructureGeneration(request);
        // @TODO Generate others

        Path rootDirectory = result.getRootDirectory();
        GitRepositoryService gitHubService = new GitRepositoryService();

        String httpTransportUrl = "https://github.com/" + loginName + "/" + artifactId;
        GitRepository gitRepository = new GitRepository();
        gitRepository.setInitialBranch("main");
        gitRepository.setHttpTransportUrl(httpTransportUrl);
        gitRepository.setOwnerName(loginName);
        gitRepository.setToken(accessToken);
        gitRepository.setTemplateFile(new File(rootDirectory.toFile().getAbsolutePath() + "/" + request.getBaseDir()));
        gitRepository.setEmail(user.getEmail());
        gitHubService.pushToGitRepository(gitRepository);

        this.projectGenerationInvoker.cleanTempFiles(result.getRootDirectory());

        return redirectUriString(request, ResultCode.CODE_SUCCESS.getCode(), httpTransportUrl);
    }

    private void checkParameters(ConnectorProjectRequest request) {
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
    public String invalidProjectRequest(IllegalArgumentException ex, HttpServletRequest httpServletRequest) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();
        for (String name : Collections.list(parameterNames)) {
            map.put(name, httpServletRequest.getParameter(name));
        }
        ObjectMapper mapper = new ObjectMapper();
        ConnectorProjectRequest request = mapper.convertValue(map, ConnectorProjectRequest.class);
        return redirectUriString(request, ResultCode.INVALID_PARAM.getCode(), ex.getMessage());
    }
}
