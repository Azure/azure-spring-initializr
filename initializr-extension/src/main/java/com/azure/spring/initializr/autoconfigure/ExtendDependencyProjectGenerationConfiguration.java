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

package com.azure.spring.initializr.autoconfigure;

import com.azure.spring.initializr.extension.dependency.springazure.SpringAzureActuatorBuildCustomizer;
import com.azure.spring.initializr.extension.dependency.springazure.SpringAzureDefaultBuildCustomizer;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.metadata.InitializrMetadata;
import org.springframework.context.annotation.Bean;

/**
 * {@link ProjectGenerationConfiguration} for customizations relevant to selected
 * dependencies.
 *
 */
@ProjectGenerationConfiguration
public class ExtendDependencyProjectGenerationConfiguration {

	private final InitializrMetadata metadata;

	public ExtendDependencyProjectGenerationConfiguration(InitializrMetadata metadata) {
		this.metadata = metadata;
	}

	@Bean
	public SpringAzureDefaultBuildCustomizer springAzureBuildCustomizer() {
		return new SpringAzureDefaultBuildCustomizer();
	}

	@Bean
	public SpringAzureActuatorBuildCustomizer springAzureActuatorBuildCustomizer() {
		return new SpringAzureActuatorBuildCustomizer();
	}



}
