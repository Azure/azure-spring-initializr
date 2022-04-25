/*
 * Copyright 2012-2021 the original author or authors.
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
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.generator.version.VersionReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;

import static com.azure.spring.initializr.extension.dependency.springazure.SpringCloudAzureNativeConfigurationVersionResolver.resolve;
import static io.spring.initializr.generator.buildsystem.Dependency.withCoordinates;

/**
 * An abstract {@link BuildCustomizer} class that automatically adds "com.azure.spring:spring-cloud-azure-native-configuration" when relevant Spring Cloud Azure
 * library and Spring Native are selected.
 */
abstract class AbstractSpringCloudAzureNativeBuildCustomizer<T extends Build> implements BuildCustomizer<T>, Ordered {

    private static final Log logger = LogFactory.getLog(AbstractSpringCloudAzureNativeBuildCustomizer.class);

    private final Version platformVersion;
    private final SpringCloudAzureNativeConfigurationSupportValidator<T> supportValidator;

    public AbstractSpringCloudAzureNativeBuildCustomizer(Version platformVersion) {
        this.platformVersion = platformVersion;
        this.supportValidator = new SpringCloudAzureNativeConfigurationSupportValidator<T>();
    }

    @Override
	public void customize(T build) {
        if (supportValidator.unSupport(build)) {
            logger.debug("Spring Cloud Azure coordinates do not support Spring Native yet.");
            return;
        }

        Dependency.Builder<?> builder = withCoordinates("com.azure.spring", "spring-cloud-azure-native-configuration");
        String azureNativeVersion = resolve(platformVersion.toString());
        build.properties()
             .version(VersionProperty.of("spring-cloud-azure-native-configuration.version"), azureNativeVersion);
        build.dependencies()
                .add("azure-native-configuration",
                    builder.version(VersionReference.ofProperty("spring-cloud-azure-native-configuration.version")));
	}

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 10;
    }
}
