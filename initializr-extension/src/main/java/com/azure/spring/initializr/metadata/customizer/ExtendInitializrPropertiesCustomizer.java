package com.azure.spring.initializr.metadata.customizer;

import com.azure.spring.initializr.autoconfigure.ExtendInitializrProperties;
import com.azure.spring.initializr.metadata.ExtendInitializrMetadata;
import org.springframework.core.Ordered;

public class ExtendInitializrPropertiesCustomizer implements ExtendInitializrMetadataCustomizer, Ordered {
    private final ExtendInitializrProperties properties;

    public ExtendInitializrPropertiesCustomizer(ExtendInitializrProperties properties) {
        this.properties = properties;
    }

    @Override
    public int getOrder() {
        //This should be applied AFTER InitializrPropertiesCustomizer
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }

    @Override
    public ExtendInitializrMetadata customize(ExtendInitializrMetadata metadata) {
        metadata.getArchitecture().merge(this.properties.getArchitectures());
        return metadata;
    }
}
