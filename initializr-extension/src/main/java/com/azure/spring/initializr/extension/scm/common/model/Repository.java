package com.azure.spring.initializr.extension.scm.common.model;


public class Repository {
    private String name;

    private String workSpace;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkSpace() {
        return workSpace;
    }

    public void setWorkSpace(String workSpace) {
        this.workSpace = workSpace;
    }
}
