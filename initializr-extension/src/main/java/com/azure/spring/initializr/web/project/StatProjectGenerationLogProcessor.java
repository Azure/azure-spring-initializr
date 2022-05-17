package com.azure.spring.initializr.web.project;

/**
 * Statistical project generation log processor interface.
 */
public interface StatProjectGenerationLogProcessor {

    void sendSuccess(String json);

    void sendFailure(String json);
}
