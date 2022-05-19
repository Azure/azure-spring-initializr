package com.azure.spring.initializr.web.project;

import io.spring.initializr.web.project.ProjectFailedEvent;
import io.spring.initializr.web.project.ProjectGeneratedEvent;

/**
 * The processor interface processes the project generation log statistical.
 */
public interface ProjectGenerationStatisticsProcessor {

    default void process(ProjectFailedEvent failedEvent) {

    }

    default void process(ProjectGeneratedEvent generatedEvent) {

    }
}
