package com.azure.spring.initializr.metadata.customizer;

import com.azure.spring.initializr.metadata.ExtendInitializrMetadata;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

public class ApplyDefaultCustomizer implements ExtendInitializrMetadataCustomizer, Ordered {
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    @Override
    public ExtendInitializrMetadata customize(ExtendInitializrMetadata metadata) {
        if (!StringUtils.hasText(metadata.getName().getContent())) {
            metadata.getName().setContent("demo");
        }
        if (!StringUtils.hasText(metadata.getDescription().getContent())) {
            metadata.getDescription().setContent("Demo project for Spring Boot");
        }
        if (!StringUtils.hasText(metadata.getGroupId().getContent())) {
            metadata.getGroupId().setContent("com.example");
        }
        if (!StringUtils.hasText(metadata.getVersion().getContent())) {
            metadata.getVersion().setContent("0.0.1-SNAPSHOT");
        }
        return metadata;
    }
}
