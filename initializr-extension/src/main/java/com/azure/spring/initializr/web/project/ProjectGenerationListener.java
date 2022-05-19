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

    private final ProjectGenerationStatisticsProcessor statisticsProcessor;

    public ProjectGenerationListener(ProjectGenerationStatisticsProcessor statisticsProcessor) {
        if (statisticsProcessor != null) {
            this.statisticsProcessor = statisticsProcessor;
        } else {
            LOGGER.warn("No 'ProjectGenerationStatisticsProcessor' bean available.");
            this.statisticsProcessor = new ProjectGenerationStatisticsProcessor(){};
        }
    }

    @Async
    @EventListener
    public void onProjectFailedEvent(ProjectFailedEvent event) {
        statisticsProcessor.process(event);
    }

    @Async
    @EventListener
    public void onProjectGeneratedEvent(ProjectGeneratedEvent event) {
        statisticsProcessor.process(event);
    }
}
