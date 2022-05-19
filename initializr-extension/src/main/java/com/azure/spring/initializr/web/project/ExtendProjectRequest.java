package com.azure.spring.initializr.web.project;

import io.spring.initializr.web.project.ProjectRequest;

import java.util.ArrayList;
import java.util.List;

public class ExtendProjectRequest extends ProjectRequest {
    private String architecture;
    private List<String> samples = new ArrayList<>();

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
}
