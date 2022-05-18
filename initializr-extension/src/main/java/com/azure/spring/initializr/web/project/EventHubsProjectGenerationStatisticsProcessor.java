package com.azure.spring.initializr.web.project;


import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * Using Event Hubs to distribute statistical project generation logs.
 */
public class EventHubsProjectGenerationStatisticsProcessor implements ProjectGenerationStatisticsProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventHubsProjectGenerationStatisticsProcessor.class);
    private final EventHubProducerClient producerClient;

    public EventHubsProjectGenerationStatisticsProcessor(EventHubProducerClient producerClient) {
        LOGGER.info("Project generation listener uses the Event Hubs processor implementation to distribute logs.");
        this.producerClient = producerClient;
    }

    @Override
    public void sendRequest(String json) {
        if (producerClient != null) {
            producerClient.send(Arrays.asList(new EventData(json)));
        }
    }
}
