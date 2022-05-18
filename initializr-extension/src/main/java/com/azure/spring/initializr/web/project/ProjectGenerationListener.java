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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.web.project.ProjectFailedEvent;
import io.spring.initializr.web.project.ProjectGeneratedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

/**
 * This listens for {@link ProjectGeneratedEvent} and {@link ProjectFailedEvent}.
 */
public class ProjectGenerationListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectGenerationListener.class);
    private final ObjectMapper objectMapper;
    private final ProjectGenerationStatisticsProcessor statisticsProcessor;

    public ProjectGenerationListener(ObjectMapper objectMapper,
                                     ProjectGenerationStatisticsProcessor statisticsProcessor) {
        this.objectMapper = objectMapper;
        this.statisticsProcessor = statisticsProcessor;
    }

    @Async
    @EventListener
    public void onProjectFailedEvent(ProjectFailedEvent event) {
        try {
            String failedEventLogString = getProjectFailedEventLogString(event);
            if (statisticsProcessor != null) {
                statisticsProcessor.sendRequest(failedEventLogString);
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
            if (statisticsProcessor != null) {
                statisticsProcessor.sendRequest(eventLogString);
            }
            LOGGER.info("Project generated: {}", eventLogString);
        } catch (JsonProcessingException e) {
            LOGGER.error("Convert the generation event exception.", e);
        }
    }

    private String getProjectFailedEventLogString(ProjectFailedEvent event) throws JsonProcessingException {
        return objectMapper.writeValueAsString(GenerationFailedEventLogConverter.CONVERTER.convert(event));
    }

    private String getGenerationEventLogString(ProjectGeneratedEvent event) throws JsonProcessingException {
        return objectMapper.writeValueAsString(GenerationEventLogConverter.CONVERTER.convert(event));
    }
}
