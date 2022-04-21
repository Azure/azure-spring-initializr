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

public class ExtendInitializrMetadataV21JsonMapper extends ExtendInitializrMetadataV2JsonMapper{
    private final TemplateVariables dependenciesVariables;

    public ExtendInitializrMetadataV21JsonMapper() {
        this.dependenciesVariables = new TemplateVariables(
            new TemplateVariable("bootVersion", TemplateVariable.VariableType.REQUEST_PARAM));
    }

    @Override
    protected ObjectNode links(ObjectNode parent, List<Type> types, String appUrl) {
        ObjectNode links = super.links(parent, types, appUrl);
        links.set("dependencies", dependenciesLink(appUrl));
        parent.set("_links", links);
        return links;
    }

    @Override
    protected ObjectNode mapDependency(Dependency dependency) {
        ObjectNode content = mapValue(dependency);
        if (dependency.getRange() != null) {
            content.put("versionRange", formatVersionRange(dependency.getRange()));
        }
        if (dependency.getLinks() != null && !dependency.getLinks().isEmpty()) {
            content.set("_links", LinkMapper.mapLinks(dependency.getLinks()));
        }
        return content;
    }

    protected String formatVersionRange(VersionRange versionRange) {
        return versionRange.format(Version.Format.V1).toRangeString();
    }

    private ObjectNode dependenciesLink(String appUrl) {
        String uri = (appUrl != null) ? appUrl + "/dependencies" : "/dependencies";
        UriTemplate uriTemplate = UriTemplate.of(uri, this.dependenciesVariables);
        ObjectNode result = nodeFactory().objectNode();
        result.put("href", uriTemplate.toString());
        result.put("templated", true);
        return result;
    }
}
