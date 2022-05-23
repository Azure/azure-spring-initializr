package com.azure.spring.initializr.actuate.stat;

import io.spring.initializr.web.project.ProjectRequestEvent;

/**
 * The processor interface processes the project generation event statistical.
 */
@FunctionalInterface
public interface ProjectGenerationStatisticsProcessor {

    void process(ProjectRequestEvent generatedEvent);
}
