package com.azure.spring.initializr.autoconfigure;

import com.azure.spring.initializr.metadata.Architecture;
import com.azure.spring.initializr.metadata.connector.Connector;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "extend.initializr")
public class ExtendInitializrProperties {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ExtendInitializrProperties.class);


    @JsonIgnore
//    @NestedConfigurationProperty
    private final List<Architecture> architectures = new ArrayList<>();


    @JsonIgnore
    private final List<Connector> connectors = new ArrayList<>();

    public List<Architecture> getArchitectures() {
        return architectures;
    }

    public List<Connector> getConnectors() {
        return connectors;
    }

}
