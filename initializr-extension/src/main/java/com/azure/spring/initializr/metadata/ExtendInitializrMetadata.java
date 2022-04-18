package com.azure.spring.initializr.metadata;

import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.Defaultable;
import io.spring.initializr.metadata.InitializrConfiguration;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.SingleSelectCapability;

import java.util.List;
import java.util.Map;

public class ExtendInitializrMetadata extends InitializrMetadata {
    private final ArchitectureCapability architecture = new ArchitectureCapability();

    public ArchitectureCapability getArchitecture() {
        return this.architecture;
    }

    @Override
    public void merge(InitializrMetadata other) {
        super.merge(other);
        if (other instanceof ExtendInitializrMetadata) {
            ExtendInitializrMetadata otherExtent = (ExtendInitializrMetadata) other;
            this.architecture.merge(otherExtent.getArchitecture());

        }
    }
    protected ExtendInitializrMetadata(InitializrConfiguration configuration) {
        super(configuration);
    }

    @Override
    public Map<String, Object> defaults() {
        Map<String, Object> results = super.defaults();
        results.put("architecture", defaultId(this.architecture));
        return results;
    }
    @Override
    public void validate() {
        super.validate();
    }



//    public void updateArchitecutes(List<DefaultMetadataElement> versionsMetadata) {
//        this.architecture.setContent(versionsMetadata);
////        List<Version> bootVersions = this.architecture.getContent().stream().map((it) -> Version.parse(it.getId()))
////                                                      .collect(Collectors.toList());
////        VersionParser parser = new VersionParser(bootVersions);
////        this.dependencies.updateCompatibilityRange(parser);
////        this.configuration.getEnv().updateCompatibilityRange(parser);
//    }

    private static String defaultId(Defaultable<? extends DefaultMetadataElement> element) {
        DefaultMetadataElement defaultValue = element.getDefault();
        return (defaultValue != null) ? defaultValue.getId() : null;
    }
}
