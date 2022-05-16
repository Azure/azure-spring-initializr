package com.azure.spring.initializr.web.project;

import io.spring.initializr.web.project.ProjectFailedEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

public class ProjectFailedEventLogConverter implements Converter<ProjectFailedEvent, GenerationFailedEventLog> {

    public static Converter<ProjectFailedEvent, GenerationFailedEventLog> CONVERTER = new ProjectFailedEventLogConverter();

    private ProjectFailedEventLogConverter() {

    }

    @Override
    public GenerationFailedEventLog convert(ProjectFailedEvent event) {
        GenerationEventLog log = GenerationEventLogConverter.CONVERTER.convert(event);
        GenerationFailedEventLog failedLog = new GenerationFailedEventLog();
        BeanUtils.copyProperties(log, failedLog);
        failedLog.setStackTrace(event.getCause().getLocalizedMessage());
        failedLog.setMessage(event.getCause().getMessage());
        failedLog.setExceptionClass(event.getCause().getClass().getSimpleName());
        return failedLog;
    }
}
