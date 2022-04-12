/*
 * Copyright 2012-2022 the original author or authors.
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

package com.azure.spring.initializr.support;

import io.spring.initializr.web.project.ProjectFailedEvent;
import io.spring.initializr.web.project.ProjectGeneratedEvent;
import org.slf4j.Logger;
import org.springframework.context.event.EventListener;

public class ProjectGenerationListener {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ProjectGenerationListener.class);

	@EventListener
	public void onProjectFailedEvent(ProjectFailedEvent event) {
		LOGGER.info("project create failed {}", event, event.getCause());
	}

	@EventListener
	public void onProjectGeneratedEvent(ProjectGeneratedEvent event) {
		LOGGER.info("project created {}", event);
	}

}
