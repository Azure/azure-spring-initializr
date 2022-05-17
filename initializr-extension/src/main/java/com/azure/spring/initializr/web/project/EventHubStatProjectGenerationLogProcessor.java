package com.azure.spring.initializr.web.project;


import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.spring.initializr.autoconfigure.StatsProperties;
import org.slf4j.Logger;

import java.util.Arrays;

/**
 * Using Event Hub to distribute statistical project generation logs.
 */
public class EventHubStatProjectGenerationLogProcessor implements StatProjectGenerationLogProcessor {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EventHubStatProjectGenerationLogProcessor.class);
    private final EventHubProducerClient failureProducerClient;
    private final EventHubProducerClient successProducerClient;

    public EventHubStatProjectGenerationLogProcessor(StatsProperties.Eventhub properties,
                                                     EventHubClientBuilder clientBuilder) {
        LOGGER.info("Project generation listener uses the Event Hub processor implementation to distribute logs.");
        if (clientBuilder != null) {
            this.successProducerClient = clientBuilder
                .eventHubName(properties.getSuccessEventhubName())
                .buildProducerClient();
            this.failureProducerClient = clientBuilder
                .eventHubName(properties.getFailureEventhubName())
                .buildProducerClient();
        } else {
            this.failureProducerClient = null;
            this.successProducerClient = null;
        }
    }

    @Override
    public void sendSuccess(String json) {
        if (successProducerClient != null) {
            successProducerClient.send(Arrays.asList(new EventData(json)));
        }
    }

    @Override
    public void sendFailure(String json) {
        if (failureProducerClient != null) {
            failureProducerClient.send(Arrays.asList(new EventData(json)));
        }
    }
}
