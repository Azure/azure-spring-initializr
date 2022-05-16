package com.azure.spring.initializr.web.project;

import io.spring.initializr.web.project.ProjectRequestEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.converter.Converter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

public class GenerationEventLogConverter implements Converter<ProjectRequestEvent, GenerationEventLog> {

    public static Converter<ProjectRequestEvent, GenerationEventLog> CONVERTER = new GenerationEventLogConverter();

    private GenerationEventLogConverter() {

    }

    @Override
    public GenerationEventLog convert(ProjectRequestEvent event) {
        GenerationEventLog log = new GenerationEventLog();
        Instant instant = Instant.ofEpochMilli(event.getTimestamp());
        log.setTimestamp(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
        ExtendProjectRequest request = (ExtendProjectRequest) event.getProjectRequest();
        BeanUtils.copyProperties(request, log);
        log.setAppVersion(event.getProjectRequest().getVersion());
        log.setProjectType(event.getProjectRequest().getType());
        log.setHost((String) request.getParameters().get("host"));
        Optional.ofNullable(request.getSamples())
                .ifPresent(samples -> log.setSamples(String.join(",", samples)));
        Optional.ofNullable(request.getDependencies())
                .ifPresent(dependencies -> log.setDependencies(String.join(",", dependencies)));
        return log;
    }
}
