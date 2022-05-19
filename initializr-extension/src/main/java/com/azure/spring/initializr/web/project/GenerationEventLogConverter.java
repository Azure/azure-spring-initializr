package com.azure.spring.initializr.web.project;

import io.spring.initializr.web.project.ProjectRequestEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class GenerationEventLogConverter implements Converter<ProjectRequestEvent, GenerationEventLog> {

    public static Converter<ProjectRequestEvent, GenerationEventLog> CONVERTER = new GenerationEventLogConverter();

    private GenerationEventLogConverter() {

    }

    @Override
    public GenerationEventLog convert(ProjectRequestEvent event) {
        GenerationEventLog log = new GenerationEventLog();
        Instant instant = Instant.ofEpochMilli(event.getTimestamp());
        log.setTimestamp(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
        ExtendWebProjectRequest request = (ExtendWebProjectRequest) event.getProjectRequest();
        BeanUtils.copyProperties(request, log);
        log.setAppVersion(event.getProjectRequest().getVersion());
        log.setProjectType(event.getProjectRequest().getType());
        log.setHost((String) request.getParameters().get("host"));
        return log;
    }
}
