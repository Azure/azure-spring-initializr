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

package com.azure.spring.initializr.autoconfigure;

import com.azure.spring.initializr.support.AzureInitializrMetadataUpdateStrategy;
import com.azure.spring.initializr.support.ProjectGenerationListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.generator.project.ProjectDirectoryFactory;
import io.spring.initializr.web.autoconfigure.InitializrAutoConfiguration;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;


@Configuration
@EnableConfigurationProperties(AzureInitializrProperties.class)
@AutoConfigureAfter({ JacksonAutoConfiguration.class, RestTemplateAutoConfiguration.class })
@AutoConfigureBefore(InitializrAutoConfiguration.class)
public class AzureInitializrAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public ProjectDirectoryFactory projectDirectoryFactory() {
		return (description) -> Files.createTempDirectory("project-");
	}

	@Bean
	public ProjectGenerationListener projectGenerationListener() {
		return new ProjectGenerationListener();
	}
    @Bean
    public AzureInitializrMetadataUpdateStrategy initializrMetadataUpdateStrategy(
        RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        return new AzureInitializrMetadataUpdateStrategy(restTemplateBuilder.build(), objectMapper);
    }
}
