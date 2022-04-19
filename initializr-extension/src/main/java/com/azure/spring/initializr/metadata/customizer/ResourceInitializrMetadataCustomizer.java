package com.azure.spring.initializr.metadata.customizer;

import com.azure.spring.initializr.metadata.ExtendInitializrMetadata;
import com.azure.spring.initializr.metadata.ExtendInitializrMetadataBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ResourceInitializrMetadataCustomizer implements ExtendInitializrMetadataCustomizer {

    private static final Log logger = LogFactory.getLog(ResourceInitializrMetadataCustomizer.class);

    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    private final Resource resource;

    public ResourceInitializrMetadataCustomizer(Resource resource) {
        this.resource = resource;
    }

    @Override
    public ExtendInitializrMetadata customize(ExtendInitializrMetadata metadata) {
        logger.info("Loading initializr metadata from " + this.resource);
        try (InputStream in = this.resource.getInputStream()) {
            String content = StreamUtils.copyToString(in, UTF_8);
            ObjectMapper objectMapper = new ObjectMapper();
            ExtendInitializrMetadata anotherMetadata = objectMapper.readValue(content, ExtendInitializrMetadata.class);
            metadata.merge(anotherMetadata);
            return metadata;
        } catch (Exception ex) {
            throw new IllegalStateException("Cannot merge", ex);
        }
    }

}
