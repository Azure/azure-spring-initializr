package com.azure.spring.initializr.autoconfigure;

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
