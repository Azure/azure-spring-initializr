/*
 * Copyright 2012-2021 the original author or authors.
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

package com.azure.spring.initializr.extension.dependency.springazure;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

/**
 * A {@link BuildCustomizer} that follows some rules to add dependencies:<br>
 * Integration and Azure Messaging Service <br>
 * 1. Automatically adds "spring-cloud-azure-starter-integration-eventhubs" when integration and Azure Event Hubs dependencies are selected.<br>
 * 2. Automatically adds "spring-cloud-azure-starter-integration-servicebus" when integration and Azure Service Bus dependencies are selected.<br>
 * 3. Automatically adds "spring-cloud-azure-starter-integration-storage-queue" when integration and Azure Storage Queue dependencies are selected.<br>
 * <br>
 * Cloud Stream and Azure Messaging Service<br>
 * 1. Automatically adds "spring-cloud-azure-stream-binder-eventhubs" when Cloud Stream and Azure Event Hubs dependencies are selected.<br>
 * 2. Automatically adds "spring-cloud-azure-stream-binder-servicebus" when Cloud Stream and Azure Service Bus dependencies are selected.<br>
 *
 */
public class SpringAzureMessagingBuildCustomizer implements BuildCustomizer<Build> {
    private static String INTEGRATION_DEPENDENCY_ID = "integration";
    private static String CLOUD_STREAM_DEPENDENCY_ID = "cloud-stream";
    private static String AZURE_EVENT_HUBS_DEPENDENCY_ID = "azure-event-hubs";
    private static String AZURE_SERVICE_BUS_DEPENDENCY_ID = "azure-service-bus";
    private static String AZURE_STORAGE_QUEUE_DEPENDENCY_ID = "azure-storage-queue";

    @Override
    public void customize(Build build) {
        customizeServiceBus(build);
        customizeEventHubs(build);
        customizeStorageQueue(build);
    }

    private void customizeServiceBus(Build build) {
        if (build.dependencies().has(AZURE_SERVICE_BUS_DEPENDENCY_ID)) {
            if (build.dependencies().has(CLOUD_STREAM_DEPENDENCY_ID)) {
                build.dependencies().add("spring-cloud-azure-stream-binder-servicebus",
                        Dependency.withCoordinates("com.azure.spring", "spring-cloud-azure-stream-binder-servicebus"));
                build.dependencies().remove(AZURE_SERVICE_BUS_DEPENDENCY_ID);
            }
            if (build.dependencies().has(INTEGRATION_DEPENDENCY_ID)) {
                build.dependencies().add("spring-cloud-azure-starter-integration-servicebus",
                        Dependency.withCoordinates("com.azure.spring", "spring-cloud-azure-starter-integration-servicebus"));
                build.dependencies().remove(AZURE_SERVICE_BUS_DEPENDENCY_ID);
            }
        }
    }

    private void customizeEventHubs(Build build) {
        if (build.dependencies().has(AZURE_EVENT_HUBS_DEPENDENCY_ID)) {
            if (build.dependencies().has(CLOUD_STREAM_DEPENDENCY_ID)) {
                build.dependencies().add("spring-cloud-azure-stream-binder-eventhubs",
                        Dependency.withCoordinates("com.azure.spring", "spring-cloud-azure-stream-binder-eventhubs"));
                build.dependencies().remove(AZURE_EVENT_HUBS_DEPENDENCY_ID);
            }
            if (build.dependencies().has(INTEGRATION_DEPENDENCY_ID)) {
                build.dependencies().add("spring-cloud-azure-starter-integration-eventhubs",
                        Dependency.withCoordinates("com.azure.spring", "spring-cloud-azure-starter-integration-eventhubs"));
                build.dependencies().remove(AZURE_EVENT_HUBS_DEPENDENCY_ID);
            }
        }
    }

    private void customizeStorageQueue(Build build) {
        if (build.dependencies().has(AZURE_STORAGE_QUEUE_DEPENDENCY_ID)) {
            if (build.dependencies().has(INTEGRATION_DEPENDENCY_ID)) {
                build.dependencies().add("spring-cloud-azure-starter-integration-storage-queue",
                        Dependency.withCoordinates("com.azure.spring", "spring-cloud-azure-starter-integration-storage-queue"));
                build.dependencies().remove(AZURE_STORAGE_QUEUE_DEPENDENCY_ID);
            }
        }
    }
}
