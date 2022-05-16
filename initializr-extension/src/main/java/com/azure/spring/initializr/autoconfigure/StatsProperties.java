/*
 * Copyright 2012-2020 the original author or authors.
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

package com.azure.spring.initializr.autoconfigure;

import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * Statistics-related properties.
 */
public class StatsProperties {

	@NestedConfigurationProperty
	private final Eventhub eventhub = new Eventhub();

    public Eventhub getEventhub() {
        return eventhub;
    }

    /**
	 * EventHub configuration.
	 */
	public static final class Eventhub {

		/**
		 * Whether the project generation stat via EventHubs is enabled, not enabled be default.
         * If enabling this switch, the project generation request will be collected through EventHubs,
         * finally ingested into Azure Data Explorer.
		 */
        private boolean enabled;

        private String successEventhubName;

        private String failedEventhubName;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getSuccessEventhubName() {
            return successEventhubName;
        }

        public void setSuccessEventhubName(String successEventhubName) {
            this.successEventhubName = successEventhubName;
        }

        public String getFailedEventhubName() {
            return failedEventhubName;
        }

        public void setFailedEventhubName(String failedEventhubName) {
            this.failedEventhubName = failedEventhubName;
        }
    }

}
