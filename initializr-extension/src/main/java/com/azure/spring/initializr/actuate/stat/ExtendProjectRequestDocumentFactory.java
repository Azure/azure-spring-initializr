package com.azure.spring.initializr.actuate.stat;

import com.azure.spring.initializr.metadata.ExtendInitializrMetadata;
import com.azure.spring.initializr.web.project.ExtendProjectRequest;
import com.azure.spring.initializr.web.project.ExtendWebProjectRequest;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.web.project.ProjectFailedEvent;
import io.spring.initializr.web.project.ProjectRequest;
import io.spring.initializr.web.project.ProjectRequestEvent;
import io.spring.initializr.web.support.Agent;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExtendProjectRequestDocumentFactory {

    public ExtendProjectRequestDocumentFactory() {
    }

    public ExtendProjectRequestDocument createDocument(ProjectRequestEvent event) {
        ExtendInitializrMetadata metadata = (ExtendInitializrMetadata) event.getMetadata();
        ExtendProjectRequest request = (ExtendProjectRequest) event.getProjectRequest();
        ExtendProjectRequestDocument document = new ExtendProjectRequestDocument();
        Instant instant = Instant.ofEpochMilli(event.getTimestamp());
        document.setTimestamp(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
        document.setArchitecture(request.getArchitecture());
        document.setReferer(determineReferer(request));
        document.setHost(determineHost(request));
        // TODO: add samples

        document.setGroupId(request.getGroupId());
        document.setArtifactId(request.getArtifactId());
        document.setPackageName(request.getPackageName());
        document.setVersion(this.determineVersionInformation(request));
        document.setClient(this.determineClientInformation(request));
        document.setJavaVersion(request.getJavaVersion());
        if (StringUtils.hasText(request.getJavaVersion()) && metadata.getJavaVersions().get(request.getJavaVersion()) == null) {
            document.triggerError().setJavaVersion(true);
        }

        document.setLanguage(request.getLanguage());
        if (StringUtils.hasText(request.getLanguage()) && metadata.getLanguages().get(request.getLanguage()) == null) {
            document.triggerError().setLanguage(true);
        }

        document.setPackaging(request.getPackaging());
        if (StringUtils.hasText(request.getPackaging()) && metadata.getPackagings().get(request.getPackaging()) == null) {
            document.triggerError().setPackaging(true);
        }

        document.setType(request.getType());
        document.setBuildSystem(this.determineBuildSystem(request));
        if (StringUtils.hasText(request.getType()) && metadata.getTypes().get(request.getType()) == null) {
            document.triggerError().setType(true);
        }

        List<String> dependencies = new ArrayList(request.getDependencies());
        List<String> validDependencies = dependencies.stream()
                                                     .filter((id) -> metadata.getDependencies().get(id) != null)
                                                     .collect(Collectors.toList());
        document.setDependencies(new ExtendProjectRequestDocument.DependencyInformation(validDependencies));
        List<String> invalidDependencies = dependencies.stream()
                                                       .filter((id) -> !validDependencies.contains(id))
                                                       .collect(Collectors.toList());
        if (!invalidDependencies.isEmpty()) {
            document.triggerError().triggerInvalidDependencies(invalidDependencies);
        }

        if (event instanceof ProjectFailedEvent) {
            ExtendProjectRequestDocument.ErrorStateInformation errorState = document.triggerError();
            ProjectFailedEvent failed = (ProjectFailedEvent)event;
            if (failed.getCause() != null) {
                errorState.setMessage(failed.getCause().getMessage());
            }
        }

        return document;
    }

    private String determineBuildSystem(ProjectRequest request) {
        String type = request.getType();
        String[] elements = type.split("-");
        return elements.length == 2 ? elements[0] : null;
    }

    private ExtendProjectRequestDocument.VersionInformation determineVersionInformation(ProjectRequest request) {
        Version version = Version.safeParse(request.getBootVersion());
        return version != null && version.getMajor() != null ? new ExtendProjectRequestDocument.VersionInformation(version) : null;
    }

    private ExtendProjectRequestDocument.ClientInformation determineClientInformation(ProjectRequest request) {
        if (request instanceof ExtendWebProjectRequest) {
            ExtendWebProjectRequest webProjectRequest = (ExtendWebProjectRequest)request;
            Agent agent = this.determineAgent(webProjectRequest);
            String ip = this.determineIp(webProjectRequest);
            String country = this.determineCountry(webProjectRequest);
            if (agent != null || ip != null || country != null) {
                return new ExtendProjectRequestDocument.ClientInformation(agent, ip, country);
            }
        }

        return null;
    }

    private Agent determineAgent(ExtendWebProjectRequest request) {
        String userAgent = (String)request.getParameters().get("user-agent");
        return StringUtils.hasText(userAgent) ? Agent.fromUserAgent(userAgent) : null;
    }

    private String determineIp(ExtendWebProjectRequest request) {
        String candidate = (String)request.getParameters().get("cf-connecting-ip");
        return StringUtils.hasText(candidate) ? candidate : (String)request.getParameters().get("x-forwarded-for");
    }

    private String determineCountry(ExtendWebProjectRequest request) {
        String candidate = (String)request.getParameters().get("cf-ipcountry");
        return StringUtils.hasText(candidate) && !"xx".equalsIgnoreCase(candidate) ? candidate : null;
    }

    private String determineReferer(ProjectRequest request) {
        if (request instanceof ExtendWebProjectRequest) {
            ExtendWebProjectRequest webProjectRequest = (ExtendWebProjectRequest)request;
            return (String) webProjectRequest.getParameters().get("referer");
        }
        return null;
    }

    private String determineHost(ProjectRequest request) {
        if (request instanceof ExtendWebProjectRequest) {
            ExtendWebProjectRequest webProjectRequest = (ExtendWebProjectRequest)request;
            return (String) webProjectRequest.getParameters().get("host");
        }
        return null;
    }
}
