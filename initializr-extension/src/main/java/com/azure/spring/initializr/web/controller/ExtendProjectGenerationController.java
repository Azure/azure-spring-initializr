package com.azure.spring.initializr.web.controller;

import com.azure.spring.initializr.web.project.ExtendProjectRequest;
import com.azure.spring.initializr.web.project.ExtendWebProjectRequest;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.controller.ProjectGenerationController;
import io.spring.initializr.web.project.ProjectGenerationInvoker;
import io.spring.initializr.web.project.ProjectRequest;

import java.util.Map;

public class ExtendProjectGenerationController extends ProjectGenerationController<ProjectRequest> {
    public ExtendProjectGenerationController(InitializrMetadataProvider metadataProvider,
                                              ProjectGenerationInvoker<ProjectRequest> projectGenerationInvoker) {
        super(metadataProvider, projectGenerationInvoker);
    }

    @Override
    public ExtendProjectRequest projectRequest(Map<String, String> headers) {
        ExtendWebProjectRequest request = new ExtendWebProjectRequest();
        request.getParameters().putAll(headers);
        request.initialize(getMetadata());
        return request;
    }
}
