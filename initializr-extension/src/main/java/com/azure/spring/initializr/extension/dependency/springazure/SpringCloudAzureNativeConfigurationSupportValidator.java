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

package com.azure.spring.initializr.extension.dependency.springazure;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.gradle.GradleBuild;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Validate that Spring Cloud Azure coordinates support Spring Native.
 */
class SpringCloudAzureNativeConfigurationSupportValidator<T extends Build> {

    private static final Log logger = LogFactory.getLog(SpringCloudAzureNativeConfigurationSupportValidator.class);

    /**
     * Supported Spring Native coordinates set.
     */
    private static final Set<String> SUPPORTED_NATIVE_COORDINATES_SET;

    static {
        Set<String> supported = new HashSet<>();
        supported.add("com.azure.spring:spring-cloud-azure-starter-appconfiguration");
        supported.add("com.azure.spring:spring-cloud-azure-starter-eventhubs");
        supported.add("com.azure.spring:spring-cloud-azure-starter-keyvault-certificates");
        supported.add("com.azure.spring:spring-cloud-azure-starter-keyvault-secrets");
        supported.add("com.azure.spring:spring-cloud-azure-starter-storage-blob");
        supported.add("com.azure.spring:spring-cloud-azure-starter-storage-file-share");
        supported.add("com.azure.spring:spring-cloud-azure-starter-storage-queue");
        supported.add("com.azure.spring:spring-cloud-azure-starter-integration-eventhubs");
        supported.add("com.azure.spring:spring-cloud-azure-starter-integration-storage-queue");
        SUPPORTED_NATIVE_COORDINATES_SET = Collections.unmodifiableSet(supported);
    }

    boolean support(T build) {
        if (!build.dependencies().items().anyMatch(u -> u.getGroupId().equals("com.azure.spring"))) {
            return false;
        }

        if (build instanceof MavenBuild){
            MavenBuild mavenBuild = (MavenBuild) build;
            if (!mavenBuild.dependencies().has("native")) {
                return false;
            }
        } else if (build instanceof GradleBuild) {
            GradleBuild gradleBuild = (GradleBuild) build;
            if (!gradleBuild.plugins().has("org.springframework.experimental.aot")) {
                return false;
            }
        } else {
            logger.info("Not supported '" + build.getClass().getName() + "' build implementation.");
            return false;
        }

        return retainSupportCoordinates(build);
    }

    boolean unSupport(T build) {
        return !support(build);
    }

    private boolean retainSupportCoordinates(T build) {
        Set<String> selected = build.dependencies()
                                    .items()
                                    .map(d -> d.getGroupId() + ":" + d.getArtifactId())
                                    .collect(Collectors.toSet());
        selected.retainAll(SUPPORTED_NATIVE_COORDINATES_SET);
        return selected.size() > 0;
    }
}
