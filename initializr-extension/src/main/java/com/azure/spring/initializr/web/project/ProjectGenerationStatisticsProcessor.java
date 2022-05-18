package com.azure.spring.initializr.web.project;

/**
 * Statistical project generation log processor interface.
 */
public interface ProjectGenerationStatisticsProcessor {

    void sendRequest(String json);
}
