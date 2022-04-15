package com.azure.spring.initializr.metadata;

import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.Defaultable;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.SingleSelectCapability;

import java.util.Map;

public class ExtentdInitializrMetadata extends InitializrMetadata {
    private final SingleSelectCapability architecture = new SingleSelectCapability("architecture", "Application "
        + "Architecture",
        "Application Architecture Description");

    public SingleSelectCapability getArchitecture() {
        return this.architecture;
    }

    @Override
    public void merge(InitializrMetadata other) {
        super.merge(other);
        if (other instanceof ExtentdInitializrMetadata) {
            ExtentdInitializrMetadata otherExtent = (ExtentdInitializrMetadata) other;
            this.architecture.merge(otherExtent.getArchitecture());

        }
    }

    @Override
    public Map<String, Object> defaults() {
        Map<String, Object> results = super.defaults();
        results.put("architecture", defaultId(this.architecture));
        return results;
    }

    public static ExtentdInitializrMetadata create(InitializrMetadata metadata) {
        ExtentdInitializrMetadata extentdInitializrMetadata = new ExtentdInitializrMetadata();
        extentdInitializrMetadata.merge(metadata);
        return extentdInitializrMetadata;

    }

    private static String defaultId(Defaultable<? extends DefaultMetadataElement> element) {
        DefaultMetadataElement defaultValue = element.getDefault();
        return (defaultValue != null) ? defaultValue.getId() : null;
    }
}
