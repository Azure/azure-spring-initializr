package com.azure.spring.initializr.web.controller;

import com.azure.spring.initializr.extension.connector.bitbucket.restclient.BitbucketClient;
import com.azure.spring.initializr.extension.connector.bitbucket.restclient.BitbucketOAuthClient;
import com.azure.spring.initializr.extension.connector.common.exception.ConnectorException;
import com.azure.spring.initializr.extension.connector.github.GitRepositoryService;
import com.azure.spring.initializr.extension.connector.common.model.CreateRepo;
import com.azure.spring.initializr.extension.connector.github.model.User;
import com.azure.spring.initializr.extension.connector.common.model.TokenResult;
import com.azure.spring.initializr.extension.connector.common.model.GitRepository;
import com.azure.spring.initializr.extension.connector.common.restclient.ConnectorClient;
import com.azure.spring.initializr.extension.connector.common.restclient.OAuthClient;
import com.azure.spring.initializr.web.connector.ConnectorProjectRequest;
import com.azure.spring.initializr.web.connector.ResultCode;
import com.azure.spring.initializr.web.project.ExtendProjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.controller.ProjectGenerationController;
import io.spring.initializr.web.project.ProjectGenerationInvoker;
import io.spring.initializr.web.project.ProjectGenerationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import com.azure.spring.initializr.extension.connector.github.restclient.GitHubClient;
import com.azure.spring.initializr.extension.connector.github.restclient.GitHubOAuthClient;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
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
    private GitHubOAuthClient gitHubOAuthClient;

    @Autowired(required = false)
    private GitHubClient gitHubClient;

    @Autowired(required = false)
    private BitbucketOAuthClient bitbucketOAuthClient;

    @Autowired(required = false)
    private BitbucketClient bitbucketClient;

    @Override
    public ExtendProjectRequest projectRequest(Map<String, String> headers) {
        ExtendProjectRequest request = new ExtendProjectRequest();
        request.getParameters().putAll(headers);
        request.initialize(getMetadata());
        return request;
    }

    @RequestMapping(path = "/login/oauth2/code")
    public String pushToGitRepository(ConnectorProjectRequest request) {
        if ("github".equals(request.getConnectorType())) {
            return pushToGitRepo(request, gitHubClient, gitHubOAuthClient);
        } else if ("bitbucket".equals(request.getConnectorType())) {
            return pushToGitRepo(request, bitbucketClient, bitbucketOAuthClient);
        }
        return redirectUriString(request);
    }

    private String pushToGitRepo(ConnectorProjectRequest request, ConnectorClient connectorClient, OAuthClient oAuthClient) {
        if (connectorClient == null || oAuthClient == null) {
            return redirectUriString(request,
                    ResultCode.CODE_404.getCode(),
                    ResultCode.CODE_404.getMsg());
        }

        checkParameters(request);
        if (request.getBaseDir() == null) {
            request.setBaseDir(request.getName());
        }

        String authorizationCode = request.getCode();

        TokenResult tokenResult = oAuthClient.getAccessToken(authorizationCode);
        String accessToken = tokenResult.getAccessToken();
        if (accessToken == null) {
            return redirectUriString(request,
                    ResultCode.ACCESSTOKEN_EMPTY.getCode(),
                    tokenResult.getError());
        }

        User user = connectorClient.getUser(accessToken);
        String loginName = user.getLogin();
        String artifactId = request.getArtifactId();
        boolean repositoryExists = connectorClient.repositoryExists(accessToken, loginName, artifactId);

        if (repositoryExists) {
            return redirectUriString(request,
                    ResultCode.CODE_REPO_ALREADY_EXISTS.getCode(),
                    "There is already a project named ' "
                            + artifactId
                            + "' on your " + request.getConnectorType()
                            + ", please retry with a different name (the artifact is the name)...");
        }

        CreateRepo repo = new CreateRepo();
        repo.setName(artifactId);
        repo.setWorkSpace(loginName);
        connectorClient.createRepo(accessToken, repo);

        ProjectGenerationResult result = this.projectGenerationInvoker.invokeProjectStructureGeneration(request);
        String gitRepositoryUrl = "https://github.com/" + loginName + "/" + artifactId;

        try{
            GitRepository gitRepository = new GitRepository();
            gitRepository.setInitialBranch("main");
            gitRepository.setHttpTransportUrl(gitRepositoryUrl);
            gitRepository.setOwnerName(loginName);
            gitRepository.setToken(accessToken);
            gitRepository.setTemplateFile(new File(result.getRootDirectory().toFile().getAbsolutePath()
                    + "/" + request.getBaseDir()));
            GitRepositoryService.pushToGitRepository(gitRepository);
        }catch (ConnectorException connectorException){
            throw connectorException;
        }finally {
            this.projectGenerationInvoker.cleanTempFiles(result.getRootDirectory());
        }

        return redirectUriString(request, ResultCode.CODE_SUCCESS.getCode(), gitRepositoryUrl);
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

    private String redirectUriString(ConnectorProjectRequest request) {
        return redirectUriString(request, null, null);
    }

    private String redirectUriString(ConnectorProjectRequest request, String errorCode, String msg) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance();
        if (errorCode != null && msg != null) {
            uriComponentsBuilder
                    .queryParam("errorcode", errorCode)
                    .queryParam("msg", msg);
        }

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

    @ExceptionHandler(value = {IllegalArgumentException.class,ConnectorException.class})
    public String invalidProjectRequest(RuntimeException ex, HttpServletRequest httpServletRequest) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();
        for (String name : Collections.list(parameterNames)) {
            map.put(name, httpServletRequest.getParameter(name));
        }
        ObjectMapper mapper = new ObjectMapper();
        ConnectorProjectRequest request = mapper.convertValue(map, ConnectorProjectRequest.class);
        String errorCode = ResultCode.CODE_SUCCESS.getCode();
        if (ex instanceof IllegalArgumentException) {
            errorCode = ResultCode.INVALID_PARAM.getCode();
        } else if (ex instanceof ConnectorException){
            errorCode = ResultCode.CONNECTOR_EXCEPTION.getCode();
        }
        return redirectUriString(request, errorCode, ex.getMessage());
    }
}
