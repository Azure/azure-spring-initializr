package com.azure.spring.initializr.web.mapper;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionRange;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.Type;
import io.spring.initializr.web.mapper.LinkMapper;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;

import java.util.List;

public class ExtendInitializrMetadataV22JsonMapper extends ExtendInitializrMetadataV21JsonMapper{
    @Override
    protected String formatVersion(String versionId) {
        return versionId;
    }

    @Override
    protected String formatVersionRange(VersionRange versionRange) {
        return versionRange.toRangeString();
    }
}
