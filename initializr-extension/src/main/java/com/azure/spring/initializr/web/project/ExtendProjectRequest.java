package com.azure.spring.initializr.web.project;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.spring.initializr.web.project.WebProjectRequest;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtendProjectRequest extends WebProjectRequest {
    private String architecture;
    private List<String> samples = new ArrayList<>();
    private String code;
    private String gitServiceType;

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public List<String> getSamples() {
        return samples;
    }

    public void setSamples(List<String> samples) {
        this.samples = samples;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGitServiceType() {
        return gitServiceType;
    }

    public void setGitServiceType(String gitServiceType) {
        this.gitServiceType = gitServiceType;
    }
}
