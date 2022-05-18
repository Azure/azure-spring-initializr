package com.azure.spring.initializr.web.project;

import io.spring.initializr.web.project.ProjectFailedEvent;
import org.springframework.core.convert.converter.Converter;

public final class GenerationFailedEventLogConverter implements Converter<ProjectFailedEvent, GenerationEventLog> {

    public static Converter<ProjectFailedEvent, GenerationEventLog> CONVERTER = new GenerationFailedEventLogConverter();

    private GenerationFailedEventLogConverter() {

    }

    @Override
    public GenerationEventLog convert(ProjectFailedEvent event) {
        GenerationEventLog log = GenerationEventLogConverter.CONVERTER.convert(event);
        log.setStackTrace(event.getCause().getLocalizedMessage());
        log.setMessage(event.getCause().getMessage());
        log.setExceptionClass(event.getCause().getClass().getSimpleName());
        return log;
    }
}
