package com.azure.spring.initializr.web.project;


import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.web.project.ProjectFailedEvent;
import io.spring.initializr.web.project.ProjectGeneratedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * The Event Hubs processor implements statistical processing of the project generation logs.
 */
public class EventHubsProjectGenerationStatisticsProcessor implements ProjectGenerationStatisticsProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventHubsProjectGenerationStatisticsProcessor.class);
    private final ObjectMapper objectMapper;
    private final EventHubProducerClient producerClient;

    public EventHubsProjectGenerationStatisticsProcessor(ObjectMapper objectMapper,
                                                         EventHubProducerClient producerClient) {
        LOGGER.info("Project generation listener will use this processor to distribute generation logs.");
        if (producerClient == null) {
            LOGGER.warn("No 'EventHubProducerClient' instance available.");
        }
        this.objectMapper = objectMapper;
        this.producerClient = producerClient;
    }

    @Override
    public void process(ProjectGeneratedEvent event) {
        try {
            String log = objectMapper.writeValueAsString(GenerationEventLogConverter.CONVERTER.convert(event));
            if (producerClient != null) {
                producerClient.send(Arrays.asList(new EventData(log)));
            }
            LOGGER.info("Project generated: {}", log);
        } catch (JsonProcessingException e) {
            LOGGER.error("Generated event JSON processing exception.", e);
        }
    }

    @Override
    public void process(ProjectFailedEvent event) {
        try {
            String log = objectMapper.writeValueAsString(GenerationFailedEventLogConverter.CONVERTER.convert(event));
            if (producerClient != null) {
                producerClient.send(Arrays.asList(new EventData(log)));
            }
            LOGGER.error("Generation failed.", event.getCause());
            LOGGER.info("Project generation failed: {}", log);
        } catch (JsonProcessingException e) {
            LOGGER.error("Failed generation event JSON processing exception.", e);
        }
    }
}
