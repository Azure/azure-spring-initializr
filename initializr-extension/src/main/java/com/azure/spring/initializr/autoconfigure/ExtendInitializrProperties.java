package com.azure.spring.initializr.autoconfigure;

import com.azure.spring.initializr.metadata.Architecture;
import com.azure.spring.initializr.metadata.scm.push.GitPush;
import com.azure.spring.initializr.metadata.scm.push.OAuthApp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "extend.initializr")
public class ExtendInitializrProperties {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ExtendInitializrProperties.class);

    @JsonIgnore
    private final List<Architecture> architectures = new ArrayList<>();

    @JsonIgnore
    private final GitPush gitPush = new GitPush();
    
    public List<Architecture> getArchitectures() {
        return architectures;
    }

    public GitPush getGitPush() {
        return gitPush;
    }

    public Map<String, OAuthApp> getOAuthApps() {
        return gitPush.getoAuthApps();
    }
}
