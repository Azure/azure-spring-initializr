package com.azure.spring.initializr.metadata.customizer;

import com.azure.spring.initializr.metadata.ExtendInitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataCustomizer;

public interface ExtendInitializrMetadataCustomizer  {
    public ExtendInitializrMetadata customize(ExtendInitializrMetadata metadata);
}
