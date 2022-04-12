/*
 * Copyright 2012-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.azure.spring.initializr.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.web.support.SaganInitializrMetadataUpdateStrategy;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * We don't provide the latest version of spring boot, but only tested spring boot versions.
 *
 * Nor we provide SNAPSHOT or Milestone versions.
 */
public class AzureInitializrMetadataUpdateStrategy extends SaganInitializrMetadataUpdateStrategy {

    public AzureInitializrMetadataUpdateStrategy(RestTemplate restTemplate, ObjectMapper objectMapper) {
        super(restTemplate, objectMapper);
    }

    @Override
    public InitializrMetadata update(InitializrMetadata current) {
//        String url = current.getConfiguration().getEnv().getSpringBootMetadataUrl();
//        List<DefaultMetadataElement> bootVersions = fetchSpringBootVersions(url);
//        if (bootVersions != null && !bootVersions.isEmpty()) {
//            if (bootVersions.stream().noneMatch(DefaultMetadataElement::isDefault)) {
//                // No default specified
//                bootVersions.get(0).setDefault(true);
//            }
//            current.updateSpringBootVersions(bootVersions);
//        }
        return current;
    }

    @Override
    protected List<DefaultMetadataElement> fetchSpringBootVersions(String url) {
        List<DefaultMetadataElement> versions = super.fetchSpringBootVersions(url);
        return (versions != null) ? versions.stream().filter(this::isCompatibleVersion).collect(Collectors.toList())
            : null;
    }

    private boolean isCompatibleVersion(DefaultMetadataElement versionMetadata) {
        Version version = Version.parse(versionMetadata.getId());
        return (version.getMajor() == 2 && version.getMinor() > 4) || (version.getMajor() >= 3);
    }

}
