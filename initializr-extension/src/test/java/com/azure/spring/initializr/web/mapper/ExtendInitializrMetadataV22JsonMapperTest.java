package com.azure.spring.initializr.web.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.spring.initializr.generator.test.InitializrMetadataTestBuilder;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.web.mapper.InitializrMetadataV22JsonMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExtendInitializrMetadataV22JsonMapperTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final ExtendInitializrMetadataV22JsonMapper jsonMapper = new ExtendInitializrMetadataV22JsonMapper();

    @Test
    void versionRangesUsingSemVerIsNotChanged() {
        Dependency dependency = Dependency.withId("test");
        dependency.setCompatibilityRange("[1.1.1-RC1,1.2.0-M1)");
        dependency.resolve();
        ObjectNode node = this.jsonMapper.mapDependency(dependency);
        assertThat(node.get("versionRange").textValue()).isEqualTo("[1.1.1-RC1,1.2.0-M1)");
    }

    @Test
    void versionRangesUsingSemVerSnapshotIsNotChanged() {
        Dependency dependency = Dependency.withId("test");
        dependency.setCompatibilityRange("1.2.0-SNAPSHOT");
        dependency.resolve();
        ObjectNode node = this.jsonMapper.mapDependency(dependency);
        assertThat(node.get("versionRange").textValue()).isEqualTo("1.2.0-SNAPSHOT");
    }

    @Test
    void platformVersionUsingSemVerUIsNotChanged() throws JsonProcessingException {
        InitializrMetadata metadata = new InitializrMetadataTestBuilder().addBootVersion("2.5.0-SNAPSHOT", false)
                                                                         .addBootVersion("2.5.0-M2", false).addBootVersion("2.4.2", true).build();
        String json = this.jsonMapper.write(metadata, null);
        JsonNode result = objectMapper.readTree(json);
        JsonNode platformVersions = result.get("bootVersion");
        assertThat(platformVersions.get("default").textValue()).isEqualTo("2.4.2");
        JsonNode versions = platformVersions.get("values");
        assertThat(versions).hasSize(3);
        assertVersionMetadata(versions.get(0), "2.5.0-SNAPSHOT", "2.5.0-SNAPSHOT");
        assertVersionMetadata(versions.get(1), "2.5.0-M2", "2.5.0-M2");
        assertVersionMetadata(versions.get(2), "2.4.2", "2.4.2");
    }

    private void assertVersionMetadata(JsonNode node, String id, String name) {
        assertThat(node.get("id").textValue()).isEqualTo(id);
        assertThat(node.get("name").textValue()).isEqualTo(name);
    }

    private Object get(JsonNode result, String path) {
        String[] nodes = path.split("\\.");
        for (int i = 0; i < nodes.length - 1; i++) {
            String node = nodes[i];
            result = result.path(node);
        }
        return result.get(nodes[nodes.length - 1]).textValue();
    }
}
