package com.azure.spring.initializr.generator.project;

import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.project.MutableProjectDescription;
import io.spring.initializr.generator.project.ProjectDescription;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExtendProjectDescription extends MutableProjectDescription {

    private final Map<String, Dependency> requestedDependencies = new LinkedHashMap<>();

    public ExtendProjectDescription(ProjectDescription projectDescription) {
        super.setPlatformVersion(projectDescription.getPlatformVersion());
        super.setBuildSystem(projectDescription.getBuildSystem());
        super.setPackaging(projectDescription.getPackaging());
        super.setLanguage(projectDescription.getLanguage());
        this.requestedDependencies.putAll(projectDescription.getRequestedDependencies());
        super.setGroupId(projectDescription.getGroupId());
        super.setArtifactId(projectDescription.getArtifactId());
        super.setVersion(projectDescription.getVersion());
        super.setName(projectDescription.getName());
        super.setDescription(projectDescription.getDescription());
        super.setApplicationName(projectDescription.getApplicationName());
        super.setPackageName(projectDescription.getPackageName());
        super.setBaseDirectory(projectDescription.getBaseDirectory());
    }

    @Override
    public Map<String, Dependency> getRequestedDependencies() {
        return Collections.unmodifiableMap(this.requestedDependencies);
    }

    private String gitServiceType;

    public String getGitServiceType() {
        return gitServiceType;
    }

    public void setGitServiceType(String gitServiceType) {
        this.gitServiceType = gitServiceType;
    }
}
