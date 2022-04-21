package com.azure.spring.initializr.metadata.customizer;

import com.azure.spring.initializr.metadata.ExtendInitializrMetadata;
import io.spring.initializr.metadata.InitializrProperties;
import org.springframework.core.Ordered;

public class InitializrPropertiesCustomizer implements ExtendInitializrMetadataCustomizer, Ordered {

    private final InitializrProperties properties;

    public InitializrPropertiesCustomizer(InitializrProperties properties) {
        this.properties = properties;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public ExtendInitializrMetadata customize(ExtendInitializrMetadata metadata) {
        metadata.getDependencies().merge(this.properties.getDependencies());
        metadata.getTypes().merge(this.properties.getTypes());
        metadata.getBootVersions().merge(this.properties.getBootVersions());
        metadata.getPackagings().merge(this.properties.getPackagings());
        metadata.getJavaVersions().merge(this.properties.getJavaVersions());
        metadata.getLanguages().merge(this.properties.getLanguages());
        this.properties.getGroupId().apply(metadata.getGroupId());
        this.properties.getArtifactId().apply(metadata.getArtifactId());
        this.properties.getVersion().apply(metadata.getVersion());
        this.properties.getName().apply(metadata.getName());
        this.properties.getDescription().apply(metadata.getDescription());
        this.properties.getPackageName().apply(metadata.getPackageName());
        return metadata;
    }

}
