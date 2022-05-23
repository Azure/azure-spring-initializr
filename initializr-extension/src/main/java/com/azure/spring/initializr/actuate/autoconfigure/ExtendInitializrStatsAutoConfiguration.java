package com.azure.spring.initializr.actuate.autoconfigure;

import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.spring.initializr.actuate.stat.EventHubsProjectGenerationStatisticsProcessor;
import com.azure.spring.initializr.actuate.stat.ExtendProjectRequestDocumentFactory;
import com.azure.spring.initializr.actuate.stat.ExtendStatsProperties;
import com.azure.spring.initializr.actuate.stat.ProjectGenerationListener;
import com.azure.spring.initializr.actuate.stat.ProjectGenerationStatisticsProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.actuate.stat.ProjectRequestDocument;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} to process statistics of each generated project.
 */
@Configuration
@ConditionalOnClass(ProjectRequestDocument.class)
@EnableConfigurationProperties(ExtendStatsProperties.class)
public class ExtendInitializrStatsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ProjectGenerationListener projectGenerationListener(List<ProjectGenerationStatisticsProcessor> statisticsProcessors) {
        return new ProjectGenerationListener(statisticsProcessors);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(EventHubProducerClient.class)
    @ConditionalOnProperty(value = "extend.initializr.stats.eventhubs.enabled", havingValue = "true")
    public ProjectGenerationStatisticsProcessor projectGenerationStatisticsProcessor(ObjectMapper objectMapper,
                                                                                     ObjectProvider<EventHubProducerClient> producerClients) {
        return new EventHubsProjectGenerationStatisticsProcessor(
            new ExtendProjectRequestDocumentFactory(), objectMapper, producerClients.getIfUnique());
    }
}
