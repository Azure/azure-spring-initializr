package com.azure.spring.initializr.extension.scm.push.common;

public enum GitServiceEnum {
    GITHUB("github"),
    BITBUCKET("bitbucket"),
    GITLAB("gitlab"),
    ;

    GitServiceEnum(String name) {
        this.name = name;
    }

    private final String name;

    public String getName() {
        return name;
    }
}
