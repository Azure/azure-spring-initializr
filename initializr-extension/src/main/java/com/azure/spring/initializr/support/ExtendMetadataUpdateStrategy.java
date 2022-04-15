package com.azure.spring.initializr.support;

import com.azure.spring.initializr.metadata.ExtentdInitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.web.support.InitializrMetadataUpdateStrategy;


public class ExtendMetadataUpdateStrategy implements InitializrMetadataUpdateStrategy {
    @Override
    public InitializrMetadata update(InitializrMetadata current) {
        return ExtentdInitializrMetadata.create(current);
    }
}
