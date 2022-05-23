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

package com.azure.spring.initializr.actuate.stat;

import io.spring.initializr.web.project.ProjectFailedEvent;
import io.spring.initializr.web.project.ProjectGeneratedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * This listens for {@link ProjectGeneratedEvent} and {@link ProjectFailedEvent}.
 */
public class ProjectGenerationListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectGenerationListener.class);
    private final List<ProjectGenerationStatisticsProcessor> statisticsProcessors;

    public ProjectGenerationListener(List<ProjectGenerationStatisticsProcessor> statisticsProcessors) {
        if (CollectionUtils.isEmpty(statisticsProcessors)) {
            LOGGER.warn("No 'ProjectGenerationStatisticsProcessor' bean available.");
        }
        this.statisticsProcessors = Collections.unmodifiableList(statisticsProcessors);
    }

    @Async
    @EventListener
    public void onProjectFailedEvent(ProjectFailedEvent event) {
        statisticsProcessors.forEach(p -> p.process(event));
    }

    @Async
    @EventListener
    public void onProjectGeneratedEvent(ProjectGeneratedEvent event) {
        statisticsProcessors.forEach(p -> p.process(event));
    }
}
