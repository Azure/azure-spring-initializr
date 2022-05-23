package com.azure.spring.initializr.actuate.stat;


import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.web.project.ProjectRequestEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * The Event Hubs processor implements statistical processing of the project generation logs.
 */
public class EventHubsProjectGenerationStatisticsProcessor implements ProjectGenerationStatisticsProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventHubsProjectGenerationStatisticsProcessor.class);
    private final ExtendProjectRequestDocumentFactory documentFactory;
    private final ObjectMapper objectMapper;
    private final EventHubProducerClient producerClient;

    public EventHubsProjectGenerationStatisticsProcessor(ExtendProjectRequestDocumentFactory documentFactory,
                                                         ObjectMapper objectMapper,
                                                         EventHubProducerClient producerClient) {
        LOGGER.info("Project generation listener will use this processor to distribute generation logs.");
        this.documentFactory = documentFactory;
        if (producerClient == null) {
            LOGGER.warn("No 'EventHubProducerClient' instance available.");
        }
        this.objectMapper = objectMapper;
        this.producerClient = producerClient;
    }

    @Override
    public void process(ProjectRequestEvent event) {
        String json = null;
        try {
            ExtendProjectRequestDocument document = this.documentFactory.createDocument(event);
            json = toJson(document);
            LOGGER.debug("Processing " + json);
            if (producerClient != null) {
                producerClient.send(Arrays.asList(new EventData(json)));
            }
        }
        catch (Exception ex) {
            LOGGER.warn(String.format("Failed to send stat to Event Hub, document follows %n%n%s%n", json), ex);
        }
    }
    private String toJson(ExtendProjectRequestDocument stats) {
        try {
            return this.objectMapper.writeValueAsString(stats);
        }
        catch (JsonProcessingException ex) {
            throw new IllegalStateException("Cannot convert to JSON", ex);
        }
    }
}
