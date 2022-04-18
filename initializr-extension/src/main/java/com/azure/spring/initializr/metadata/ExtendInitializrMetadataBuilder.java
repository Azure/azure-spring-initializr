package com.azure.spring.initializr.metadata;

import com.azure.spring.initializr.metadata.customizer.ExtendInitializrMetadataCustomizer;
import io.spring.initializr.metadata.InitializrConfiguration;
import io.spring.initializr.metadata.InitializrMetadataCustomizer;

import java.util.ArrayList;
import java.util.List;

public class ExtendInitializrMetadataBuilder {
    private final List<ExtendInitializrMetadataCustomizer> customizers = new ArrayList<>();

    private final InitializrConfiguration configuration;

    public ExtendInitializrMetadataBuilder(InitializrConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Add a {@link InitializrMetadataCustomizer}. customizers are invoked in their order of addition.
     *
     * @param customizer the customizer to add
     * @return this instance
     * @see InitializrMetadataCustomizer
     */
    public ExtendInitializrMetadataBuilder withCustomizer(ExtendInitializrMetadataCustomizer customizer) {
        this.customizers.add(customizer);
        return this;
    }

    /**
     * Build a {@link ExtendInitializrMetadata} based on the state of this builder.
     *
     * @return a new {@link ExtendInitializrMetadata} instance
     */
    public ExtendInitializrMetadata build() {
        InitializrConfiguration config = (this.configuration != null) ? this.configuration
            : new InitializrConfiguration();
        ExtendInitializrMetadata metadata = new ExtendInitializrMetadata(config);
        for (ExtendInitializrMetadataCustomizer customizer : this.customizers) {
            metadata = customizer.customize(metadata);
        }
        metadata.validate();
        return metadata;
    }


}
