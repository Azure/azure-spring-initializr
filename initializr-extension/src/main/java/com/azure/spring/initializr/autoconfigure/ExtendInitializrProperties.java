package com.azure.spring.initializr.autoconfigure;

import com.azure.spring.initializr.metadata.Architecture;
import com.azure.spring.initializr.metadata.scm.OAuthApp;
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

    private final Scm scm = new Scm();

    public List<Architecture> getArchitectures() {
        return architectures;
    }

    public Scm getScm() {
        return scm;
    }

    public class Scm{
        @JsonIgnore
        private final Map<String, OAuthApp> oAuthAppMap = new HashMap();

        public Map<String, OAuthApp> getOAuthApp() {
            return oAuthAppMap;
        }
    }

    public Map<String, OAuthApp> getOAuthApp() {
        return this.scm.getOAuthApp();
    }


}
