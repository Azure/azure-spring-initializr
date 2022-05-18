package com.azure.spring.initializr.autoconfigure;

import com.azure.spring.initializr.metadata.Architecture;
import com.azure.spring.initializr.metadata.scm.push.OAuthApp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "extend.initializr")
public class ExtendInitializrProperties {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ExtendInitializrProperties.class);

    @JsonIgnore
    private final List<Architecture> architectures = new ArrayList<>();

    public List<Architecture> getArchitectures() {
        return architectures;
    }

    @JsonIgnore
    private final Map<String, OAuthApp> oAuthApps = new HashMap<>();

    public Map<String, OAuthApp> getOAuthApps() {
        return oAuthApps;
    }


}
