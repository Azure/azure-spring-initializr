package com.azure.spring.initializr.web.project;

import com.azure.spring.initializr.generator.project.ExtendProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.web.project.DefaultProjectRequestToDescriptionConverter;
import io.spring.initializr.web.project.ProjectRequest;
import io.spring.initializr.web.project.ProjectRequestPlatformVersionTransformer;
import io.spring.initializr.web.project.ProjectRequestToDescriptionConverter;

public class ExtendProjectRequestToDescriptionConverter implements ProjectRequestToDescriptionConverter<ExtendProjectRequest> {

    private final ProjectRequestPlatformVersionTransformer platformVersionTransformer;

    private final DefaultProjectRequestToDescriptionConverter delegate;

    public ExtendProjectRequestToDescriptionConverter(ProjectRequestPlatformVersionTransformer platformVersionTransformer) {
        this.platformVersionTransformer = platformVersionTransformer;
        this.delegate = new DefaultProjectRequestToDescriptionConverter(platformVersionTransformer);
    }

    @Override
    public ProjectDescription convert(ExtendProjectRequest request, InitializrMetadata metadata) {
        validate(request, metadata);
        ProjectDescription description = delegate.convert(request, metadata);
        ExtendProjectDescription extendProjectDescription = new ExtendProjectDescription(description);
        extendProjectDescription.setGitServiceType(request.getGitServiceType());
        return extendProjectDescription;
    }

    private void validate(ProjectRequest request, InitializrMetadata metadata) {
        validateArchitecture(request, metadata);
        validateSamples(request, metadata);
    }

    private void validateArchitecture(ProjectRequest request, InitializrMetadata metadata) {

    }

    private void validateSamples(ProjectRequest request, InitializrMetadata metadata) {

    }


}
