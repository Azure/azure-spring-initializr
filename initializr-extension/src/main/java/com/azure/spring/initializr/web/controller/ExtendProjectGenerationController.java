package com.azure.spring.initializr.web.controller;

import com.azure.spring.initializr.extension.scm.common.exception.OAuthAppException;
import com.azure.spring.initializr.extension.scm.common.model.Repository;
import com.azure.spring.initializr.extension.scm.common.model.User;
import com.azure.spring.initializr.extension.scm.common.service.GitService;
import com.azure.spring.initializr.extension.scm.common.service.GitServiceFactoryDelegate;
import com.azure.spring.initializr.web.scm.PushToGitProjectRequest;
import com.azure.spring.initializr.web.scm.ResultCode;
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
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ExtendProjectGenerationController extends ProjectGenerationController<ExtendProjectRequest> {

    ProjectGenerationInvoker<ExtendProjectRequest> projectGenerationInvoker;

    public ExtendProjectGenerationController(InitializrMetadataProvider metadataProvider,
                                             ProjectGenerationInvoker<ExtendProjectRequest> projectGenerationInvoker) {
        super(metadataProvider, projectGenerationInvoker);
        this.projectGenerationInvoker = projectGenerationInvoker;
    }

    @Autowired
    GitServiceFactoryDelegate gitServiceFactoryDelegate;

    @Override
    public ExtendProjectRequest projectRequest(Map<String, String> headers) {
        ExtendProjectRequest request = new ExtendProjectRequest();
        request.getParameters().putAll(headers);
        request.initialize(getMetadata());
        return request;
    }

    @RequestMapping(path = "/login/oauth2/code")
    public String pushToGitRepository(PushToGitProjectRequest request) {
        if ("github".equals(request.getConnectorType())) {
            return pushToGitRepo(request);
        }
        return redirectUriString(request);
    }

    private String pushToGitRepo(PushToGitProjectRequest request) {

        checkParameters(request);
        if (request.getBaseDir() == null) {
            request.setBaseDir(request.getName());
        }

        String authorizationCode = request.getCode();
        GitService gitService = gitServiceFactoryDelegate.getGitService(request.getConnectorType(), authorizationCode);

        User user = gitService.getUser();
        String username = user.getUsername();
        String artifactId = request.getArtifactId();
        boolean repositoryExists = gitService.repositoryExists(username, artifactId);

        if (repositoryExists) {
            return redirectUriString(request,
                    ResultCode.CODE_REPO_ALREADY_EXISTS.getCode(),
                    "There is already a project named ' "
                            + artifactId
                            + "' on your " + request.getConnectorType()
                            + ", please retry with a different name (the artifact is the name)...");
        }

        Repository repository = new Repository();
        repository.setName(artifactId);
        repository.setWorkSpace(username);
        gitService.createRepository(repository);

        ProjectGenerationResult result = this.projectGenerationInvoker.invokeProjectStructureGeneration(request);

        String gitRepositoryUrl = "https://github.com/" + username + "/" + artifactId;
        try {
            File path = new File(result.getRootDirectory().toFile().getAbsolutePath()
                    + "/" + request.getBaseDir());
            gitService.pushToGitRepository(gitService.getAccessToken(), username, path, gitRepositoryUrl);
        } catch (OAuthAppException scmException) {
            throw scmException;
        } finally {
            this.projectGenerationInvoker.cleanTempFiles(result.getRootDirectory());
        }

        return redirectUriString(request, ResultCode.CODE_SUCCESS.getCode(), gitRepositoryUrl);
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

    private String redirectUriString(PushToGitProjectRequest request) {
        return redirectUriString(request, null, null);
    }

    private String redirectUriString(PushToGitProjectRequest request, String errorCode, String msg) {
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

    @ExceptionHandler(value = {IllegalArgumentException.class, OAuthAppException.class})
    public String invalidProjectRequest(RuntimeException ex, HttpServletRequest httpServletRequest) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> parameterNames = httpServletRequest.getParameterNames();
        for (String name : Collections.list(parameterNames)) {
            map.put(name, httpServletRequest.getParameter(name));
        }
        ObjectMapper mapper = new ObjectMapper();
        PushToGitProjectRequest request = mapper.convertValue(map, PushToGitProjectRequest.class);
        String errorCode = ResultCode.CODE_SUCCESS.getCode();
        if (ex instanceof IllegalArgumentException) {
            errorCode = ResultCode.INVALID_PARAM.getCode();
        } else if (ex instanceof OAuthAppException) {
            errorCode = ResultCode.OAUTHAPP_EXCEPTION.getCode();
        }
        return redirectUriString(request, errorCode, ex.getMessage());
    }
}
