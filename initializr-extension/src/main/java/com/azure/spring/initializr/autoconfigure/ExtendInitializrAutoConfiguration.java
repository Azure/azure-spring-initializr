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

import com.azure.spring.initializr.extension.scm.push.common.GitServiceEnum;
import com.azure.spring.initializr.extension.scm.push.common.service.GitServiceFactory;
import com.azure.spring.initializr.extension.scm.push.common.service.GitServiceFactoryResolver;
import com.azure.spring.initializr.extension.scm.push.github.restclient.GithubServiceFactory;
import com.azure.spring.initializr.metadata.ExtendInitializrMetadata;
import com.azure.spring.initializr.metadata.ExtendInitializrMetadataBuilder;
import com.azure.spring.initializr.metadata.ExtendInitializrMetadataProvider;
import com.azure.spring.initializr.metadata.customizer.ApplyDefaultCustomizer;
import com.azure.spring.initializr.metadata.customizer.ExtendInitializrMetadataCustomizer;
import com.azure.spring.initializr.metadata.customizer.ExtendInitializrPropertiesCustomizer;
import com.azure.spring.initializr.metadata.customizer.InitializrPropertiesCustomizer;
import com.azure.spring.initializr.metadata.scm.push.GitPush;
import com.azure.spring.initializr.metadata.scm.push.OAuthApp;
import com.azure.spring.initializr.support.AzureInitializrMetadataUpdateStrategy;
import com.azure.spring.initializr.web.controller.ExtendProjectGenerationController;
import com.azure.spring.initializr.web.controller.ExtendProjectMetadataController;
import com.azure.spring.initializr.web.project.ExtendProjectRequest;
import com.azure.spring.initializr.web.project.ExtendProjectRequestToDescriptionConverter;
import com.azure.spring.initializr.web.project.ProjectGenerationListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.io.SimpleIndentStrategy;
import io.spring.initializr.generator.io.template.MustacheTemplateRenderer;
import io.spring.initializr.generator.io.template.TemplateRenderer;
import io.spring.initializr.generator.project.ProjectDirectoryFactory;
import io.spring.initializr.metadata.DependencyMetadataProvider;
import io.spring.initializr.metadata.InitializrConfiguration;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.metadata.InitializrProperties;
import io.spring.initializr.web.autoconfigure.InitializrWebConfig;
import io.spring.initializr.web.controller.CommandLineMetadataController;
import io.spring.initializr.web.controller.SpringCliDistributionController;
import io.spring.initializr.web.project.DefaultProjectRequestPlatformVersionTransformer;
import io.spring.initializr.web.project.ProjectGenerationInvoker;
import io.spring.initializr.web.project.ProjectRequestPlatformVersionTransformer;
import io.spring.initializr.web.support.DefaultDependencyMetadataProvider;
import io.spring.initializr.web.support.InitializrMetadataUpdateStrategy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.NoOpCache;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.nio.file.Files;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({ ExtendInitializrProperties.class, InitializrProperties.class })
@AutoConfigureAfter({ JacksonAutoConfiguration.class, RestTemplateAutoConfiguration.class })
public class ExtendInitializrAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ProjectDirectoryFactory projectDirectoryFactory() {
        return (description) -> Files.createTempDirectory("project-");
    }

    @Bean
    @ConditionalOnMissingBean
    public ProjectGenerationListener projectGenerationListener() {
        return new ProjectGenerationListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public AzureInitializrMetadataUpdateStrategy initializrMetadataUpdateStrategy(
        RestTemplateBuilder restTemplateBuilder, ObjectMapper objectMapper) {
        return new AzureInitializrMetadataUpdateStrategy(restTemplateBuilder.build(), objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    InitializrPropertiesCustomizer initializerPropertiesCustomizer(InitializrProperties properties) {
        return new InitializrPropertiesCustomizer(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    ApplyDefaultCustomizer applyDefaultCustomizer() {
        return new ApplyDefaultCustomizer();
    }

    @Bean
    @ConditionalOnMissingBean
    ExtendInitializrPropertiesCustomizer extendInitializrPropertiesCustomizer(ExtendInitializrProperties properties){
        return new ExtendInitializrPropertiesCustomizer(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    ExtendInitializrMetadataBuilder extendInitializrMetadataBuilder(
        InitializrConfiguration configuration, ObjectProvider<ExtendInitializrMetadataCustomizer> customizers) {
        ExtendInitializrMetadataBuilder builder = new ExtendInitializrMetadataBuilder(configuration);
        customizers.orderedStream().forEach(builder::withCustomizer);
        return builder;
    }

    @Bean
    @ConditionalOnMissingBean(InitializrMetadataProvider.class)
    public InitializrMetadataProvider initializrMetadataProvider(InitializrProperties properties,
                                                                 ExtendInitializrMetadataBuilder builder,
                                                                 ObjectProvider<InitializrMetadataUpdateStrategy> strategies) {
//        ExtendInitializrMetadataBuilder builder = ExtendInitializrMetadataBuilder
//            .fromInitializrProperties(properties);
//        customizers.orderedStream().forEach(builder::withCustomizer);
        ExtendInitializrMetadata metadata = builder.build();
        return new ExtendInitializrMetadataProvider(metadata, strategies);
    }

    @Bean
    GitServiceFactoryResolver gitServiceFactoryResolver(ObjectProvider<GitServiceFactory> providerFactories) {
        return new GitServiceFactoryResolver(providerFactories);
    }

    @Bean
    @ConditionalOnProperty(
            prefix = "extend.initializr.gitpush.oauthapps",
            name = "github.enabled",
            havingValue = "true")
    GitServiceFactory githubServiceFactory(ExtendInitializrProperties properties,
                                           WebClient webClient) {
        GitPush.Config gitPushConfig = properties.getGitPush().getConfig();
        OAuthApp oAuthApp = properties.getOAuthApps().get(GitServiceEnum.GITHUB.getName());

        return new GithubServiceFactory(gitPushConfig, oAuthApp, webClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public WebClient webclient() {
        return WebClient.builder().build();
    }

    @Bean
    @ConditionalOnMissingBean
    public IndentingWriterFactory indentingWriterFactory() {
        return IndentingWriterFactory.create(new SimpleIndentStrategy("\t"));
    }

    @Bean
    @ConditionalOnMissingBean(TemplateRenderer.class)
    public MustacheTemplateRenderer templateRenderer(Environment environment,
                                                     ObjectProvider<CacheManager> cacheManager) {
        return new MustacheTemplateRenderer("classpath:/templates",
            determineCache(environment, cacheManager.getIfAvailable()));
    }

    private Cache determineCache(Environment environment, CacheManager cacheManager) {
        if (cacheManager != null) {
            Binder binder = Binder.get(environment);
            boolean cache = binder.bind("spring.mustache.cache", Boolean.class).orElse(true);
            if (cache) {
                return cacheManager.getCache("initializr.templates");
            }
        }
        return new NoOpCache("templates");
    }

    @Bean
    @ConditionalOnMissingBean
    public DependencyMetadataProvider dependencyMetadataProvider() {
        return new DefaultDependencyMetadataProvider();
    }

    /** Initializr web configuration. */
    @Configuration
    @ConditionalOnWebApplication
    static class InitializrWebConfiguration {

        @Bean("projectGenerationController")
        @ConditionalOnMissingBean
        ExtendProjectGenerationController projectGenerationController(
                InitializrMetadataProvider metadataProvider,
                ObjectProvider<ProjectRequestPlatformVersionTransformer> platformVersionTransformer,
                ApplicationContext applicationContext,
                GitServiceFactoryResolver gitServiceFactoryResolver) {
            ExtendProjectRequestToDescriptionConverter converter =
                    new ExtendProjectRequestToDescriptionConverter(
                            platformVersionTransformer.getIfAvailable(
                                    DefaultProjectRequestPlatformVersionTransformer::new));
            ProjectGenerationInvoker<ExtendProjectRequest> projectGenerationInvoker =
                    new ProjectGenerationInvoker<>(applicationContext, converter);
            return new ExtendProjectGenerationController(
                    metadataProvider, projectGenerationInvoker, gitServiceFactoryResolver);
        }

        @Bean
        InitializrWebConfig initializrWebConfig() {
            return new InitializrWebConfig();
        }

        @Bean
        @ConditionalOnMissingBean
        ExtendProjectMetadataController projectMetadataController(InitializrMetadataProvider metadataProvider,
                                                                  DependencyMetadataProvider dependencyMetadataProvider,
                                                                  ExtendInitializrProperties properties) {
            return new ExtendProjectMetadataController(metadataProvider, dependencyMetadataProvider, properties);
        }

        @Bean
        @ConditionalOnMissingBean
        CommandLineMetadataController commandLineMetadataController(InitializrMetadataProvider metadataProvider,
                                                                    TemplateRenderer templateRenderer) {
            return new CommandLineMetadataController(metadataProvider, templateRenderer);
        }

        @Bean
        @ConditionalOnMissingBean
        SpringCliDistributionController cliDistributionController(InitializrMetadataProvider metadataProvider) {
            return new SpringCliDistributionController(metadataProvider);
        }

        @Bean
        InitializrModule InitializrJacksonModule() {
            return new InitializrModule();
        }
    }

    /**
     * Initializr cache configuration.
     */
    @Configuration
    @ConditionalOnClass(javax.cache.CacheManager.class)
    static class InitializrCacheConfiguration {

        @Bean
        JCacheManagerCustomizer initializrCacheManagerCustomizer() {
            return new InitializrJCacheManagerCustomizer();
        }
    }

    @Order(0)
    private static class InitializrJCacheManagerCustomizer implements JCacheManagerCustomizer {

        @Override
        public void customize(javax.cache.CacheManager cacheManager) {
            createMissingCache(cacheManager, "initializr.metadata",
                () -> config().setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.TEN_MINUTES)));
            createMissingCache(cacheManager, "initializr.dependency-metadata", this::config);
            createMissingCache(cacheManager, "initializr.project-resources", this::config);
            createMissingCache(cacheManager, "initializr.templates", this::config);
        }

        private void createMissingCache(javax.cache.CacheManager cacheManager, String cacheName,
                                        Supplier<MutableConfiguration<Object, Object>> config) {
            boolean cacheExist = StreamSupport.stream(cacheManager.getCacheNames().spliterator(), true)
                                              .anyMatch((name) -> name.equals(cacheName));
            if (!cacheExist) {
                cacheManager.createCache(cacheName, config.get());
            }
        }

        private MutableConfiguration<Object, Object> config() {
            return new MutableConfiguration<>().setStoreByValue(false).setManagementEnabled(true)
                                               .setStatisticsEnabled(true);
        }
    }
}
