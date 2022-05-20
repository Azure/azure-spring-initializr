package com.azure.spring.initializr.actuate.stat;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.web.support.Agent;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class ExtendProjectRequestDocument {

    @JsonProperty("EventDate")
    private LocalDateTime timestamp;

    @JsonProperty("Architecture")
    private String architecture;

    @JsonProperty("Host")
    private String host;

    @JsonProperty("Referer")
    private String referer;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("BuildSystem")
    private String buildSystem;

    @JsonProperty("GroupId")
    private String groupId;

    @JsonProperty("ArtifactId")
    private String artifactId;

    @JsonProperty("JavaVersion")
    private String javaVersion;

    @JsonProperty("Language")
    private String language;

    @JsonProperty("Packaging")
    private String packaging;

    @JsonProperty("PackageName")
    private String packageName;

    @JsonProperty("Version")
    private VersionInformation version;

    @JsonProperty("Client")
    private ClientInformation client;

    @JsonProperty("Dependencies")
    private DependencyInformation dependencies;

    @JsonProperty("Samples")
    private ExtendProjectRequestDocument.SampleInformation samples;

    @JsonProperty("ErrorState")
    private ErrorStateInformation errorState;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBuildSystem() {
        return buildSystem;
    }

    public void setBuildSystem(String buildSystem) {
        this.buildSystem = buildSystem;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPackaging() {
        return packaging;
    }

    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public VersionInformation getVersion() {
        return version;
    }

    public void setVersion(VersionInformation version) {
        this.version = version;
    }

    public ClientInformation getClient() {
        return client;
    }

    public void setClient(ClientInformation client) {
        this.client = client;
    }

    public DependencyInformation getDependencies() {
        return dependencies;
    }

    public void setDependencies(DependencyInformation dependencies) {
        this.dependencies = dependencies;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public SampleInformation getSamples() {
        return samples;
    }

    public void setSamples(SampleInformation samples) {
        this.samples = samples;
    }

    public ErrorStateInformation getErrorState() {
        return errorState;
    }

    public ErrorStateInformation triggerError() {
        if (this.errorState == null) {
            this.errorState = new ErrorStateInformation();
        }

        return this.errorState;
    }

    public static class InvalidDependencyInformation {

        @JsonProperty("Invalid")
        private boolean invalid = true;

        @JsonProperty("Values")
        private final List<String> values;

        public InvalidDependencyInformation(List<String> values) {
            this.values = values;
        }

        public boolean isInvalid() {
            return this.invalid;
        }

        public List<String> getValues() {
            return this.values;
        }

        public String toString() {
            return (new StringJoiner(", ", "{", "}")).add(String.join(", ", this.values)).toString();
        }
    }

    public static class ErrorStateInformation {

        @JsonProperty("Invalid")
        private boolean invalid = true;

        @JsonProperty("JavaVersion")
        private Boolean javaVersion;

        @JsonProperty("Language")
        private Boolean language;

        @JsonProperty("Packaging")
        private Boolean packaging;

        @JsonProperty("Type")
        private Boolean type;

        @JsonProperty("Dependencies")
        private InvalidDependencyInformation dependencies;

        @JsonProperty("Message")
        private String message;

        public ErrorStateInformation() {
        }

        public boolean isInvalid() {
            return this.invalid;
        }

        public Boolean getJavaVersion() {
            return this.javaVersion;
        }

        public void setJavaVersion(Boolean javaVersion) {
            this.javaVersion = javaVersion;
        }

        public Boolean getLanguage() {
            return this.language;
        }

        public void setLanguage(Boolean language) {
            this.language = language;
        }

        public Boolean getPackaging() {
            return this.packaging;
        }

        public void setPackaging(Boolean packaging) {
            this.packaging = packaging;
        }

        public Boolean getType() {
            return this.type;
        }

        public void setType(Boolean type) {
            this.type = type;
        }

        public InvalidDependencyInformation getDependencies() {
            return this.dependencies;
        }

        public void triggerInvalidDependencies(List<String> dependencies) {
            this.dependencies = new InvalidDependencyInformation(dependencies);
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String toString() {
            return (new StringJoiner(", ", "{", "}")).add("invalid=" + this.invalid).add("javaVersion=" + this.javaVersion).add("language=" + this.language).add("packaging=" + this.packaging).add("type=" + this.type).add("dependencies=" + this.dependencies).add("message='" + this.message + "'").toString();
        }
    }

    public static class ClientInformation {

        @JsonProperty("Id")
        private final String id;

        @JsonProperty("Version")
        private final String version;

        @JsonProperty("Ip")
        private final String ip;

        @JsonProperty("Country")
        private final String country;

        public ClientInformation(Agent agent, String ip, String country) {
            this.id = agent != null ? agent.getId().getId() : null;
            this.version = agent != null ? agent.getVersion() : null;
            this.ip = ip;
            this.country = country;
        }

        public String getId() {
            return this.id;
        }

        public String getVersion() {
            return this.version;
        }

        public String getIp() {
            return this.ip;
        }

        public String getCountry() {
            return this.country;
        }

        public String toString() {
            return (new StringJoiner(", ", "{", "}")).add("id='" + this.id + "'").add("version='" + this.version + "'").add("ip='" + this.ip + "'").add("country='" + this.country + "'").toString();
        }
    }

    public static class DependencyInformation {

        @JsonProperty("Id")
        private final String id;

        @JsonProperty("Values")
        private final List<String> values;

        @JsonProperty("Count")
        private final int count;

        public DependencyInformation(List<String> values) {
            this.id = computeDependenciesId(new ArrayList(values));
            this.values = values;
            this.count = values.size();
        }

        public String getId() {
            return this.id;
        }

        public List<String> getValues() {
            return this.values;
        }

        public int getCount() {
            return this.count;
        }

        private static String computeDependenciesId(List<String> dependencies) {
            if (ObjectUtils.isEmpty(dependencies)) {
                return "_none";
            } else {
                Collections.sort(dependencies);
                return StringUtils.collectionToDelimitedString(dependencies, " ");
            }
        }

        public String toString() {
            return (new StringJoiner(", ", "{", "}")).add("id='" + this.id + "'").add("values=" + this.values).add("count=" + this.count).toString();
        }
    }

    public static class VersionInformation {

        @JsonProperty("Id")
        private final String id;

        @JsonProperty("Major")
        private final String major;

        @JsonProperty("Minor")
        private final String minor;

        public VersionInformation(Version version) {
            this.id = version.toString();
            this.major = String.format("%s", version.getMajor());
            this.minor = version.getMinor() != null ? String.format("%s.%s", version.getMajor(), version.getMinor()) : null;
        }

        public String getId() {
            return this.id;
        }

        public String getMajor() {
            return this.major;
        }

        public String getMinor() {
            return this.minor;
        }

        public String toString() {
            return (new StringJoiner(", ", "{", "}")).add("id='" + this.id + "'").add("major='" + this.major + "'").add("minor='" + this.minor + "'").toString();
        }
    }

    public static class SampleInformation {

        @JsonProperty("Id")
        private final String id;

        @JsonProperty("Values")
        private final List<String> values;

        @JsonProperty("Count")
        private final int count;

        public SampleInformation(List<String> values) {
            this.id = computeSamplesId(new ArrayList(values));
            this.values = values;
            this.count = values.size();
        }

        public String getId() {
            return this.id;
        }

        public List<String> getValues() {
            return this.values;
        }

        public int getCount() {
            return this.count;
        }

        private static String computeSamplesId(List<String> samples) {
            if (ObjectUtils.isEmpty(samples)) {
                return "_none";
            } else {
                Collections.sort(samples);
                return StringUtils.collectionToDelimitedString(samples, " ");
            }
        }

        public String toString() {
            return (new StringJoiner(", ", "{", "}")).add("id='" + this.id + "'").add("values=" + this.values).add("count=" + this.count).toString();
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ExtendProjectRequestDocument.class.getSimpleName() + "[", "]")
            .add("timestamp=" + getTimestamp() + "'")
            .add("host=" + this.host + "'")
            .add("referer=" + this.referer + "'")
            .add("architecture=" + this.architecture + "'")
            .add("samples=" + this.samples + "'")
            .add("type='" + this.type + "'")
            .add("buildSystem='" + this.buildSystem + "'")
            .add("groupId='" + this.groupId + "'")
            .add("artifactId='" + this.artifactId + "'")
            .add("javaVersion='" + this.javaVersion + "'")
            .add("language='" + this.language + "'")
            .add("packaging='" + this.packaging + "'")
            .add("packageName='" + this.packageName + "'")
            .add("version=" + this.version + "'")
            .add("client=" + this.client + "'")
            .add("dependencies=" + this.dependencies + "'")
            .add("errorState=" + this.errorState + "'")
            .toString();
    }
}
