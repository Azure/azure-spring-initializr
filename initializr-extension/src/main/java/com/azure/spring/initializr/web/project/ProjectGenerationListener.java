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

package com.azure.spring.initializr.web.project;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.spring.initializr.autoconfigure.ExtendInitializrProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.web.project.ProjectFailedEvent;
import io.spring.initializr.web.project.ProjectGeneratedEvent;
import org.slf4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * This listens for {@link ProjectGeneratedEvent} and {@link ProjectFailedEvent}.
 *
 * TODO we can use this listener to track the statistic of project generation.
 */
public class ProjectGenerationListener {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ProjectGenerationListener.class);
    private static final String INITIALIZR_SUCCESS_EVENTHUB_NAME = "initializr-success";
    private static final String INITIALIZR_FAILED_EVENTHUB_NAME = "initializr-failed";
    private final ObjectMapper objectMapper;
    private final ExtendInitializrProperties properties;
    private final EventHubProducerClient failedProducerClient;
    private final EventHubProducerClient successProducerClient;

    public ProjectGenerationListener(ObjectMapper objectMapper,
                                     ExtendInitializrProperties properties,
                                     EventHubClientBuilder clientBuilder) {
        this.objectMapper = objectMapper;
        this.properties = properties;
        if (clientBuilder != null) {
            String success = properties.getStats().getEventhub().getSuccessEventhubName();
            String failed = properties.getStats().getEventhub().getFailedEventhubName();
            this.failedProducerClient = clientBuilder
                .eventHubName(StringUtils.hasText(success) ? success : INITIALIZR_FAILED_EVENTHUB_NAME)
                .buildProducerClient();
            this.successProducerClient = clientBuilder
                .eventHubName(StringUtils.hasText(failed) ? failed : INITIALIZR_SUCCESS_EVENTHUB_NAME)
                .buildProducerClient();
        } else {
            this.failedProducerClient = null;
            this.successProducerClient = null;
        }
    }

    @Async
	@EventListener
	public void onProjectFailedEvent(ProjectFailedEvent event) {
        try {
            String failedEventLogString = getProjectFailedEventLogString(event);
            if (failedProducerClient != null) {
                failedProducerClient.send(Arrays.asList(new EventData(failedEventLogString)));
            }
            LOGGER.error("Generation failed.", event.getCause());
            LOGGER.info("Project generation failed: {}", failedEventLogString);
        } catch (JsonProcessingException e) {
            LOGGER.error("Convert the failed generation event exception.", e);
        }
    }

    @Async
	@EventListener
	public void onProjectGeneratedEvent(ProjectGeneratedEvent event) {
        try {
            String eventLogString = getGenerationEventLogString(event);
            if (successProducerClient != null) {
                successProducerClient.send(Arrays.asList(new EventData(eventLogString)));
            }
            LOGGER.info("Project generated: {}", eventLogString);
        } catch (JsonProcessingException e) {
            LOGGER.error("Convert the generation event exception.", e);
        }
	}

    private String getProjectFailedEventLogString(ProjectFailedEvent event) throws JsonProcessingException {
        return objectMapper.writeValueAsString(ProjectFailedEventLogConverter.CONVERTER.convert(event));
    }

    private String getGenerationEventLogString(ProjectGeneratedEvent event) throws JsonProcessingException {
        return objectMapper.writeValueAsString(GenerationEventLogConverter.CONVERTER.convert(event));
    }
}
