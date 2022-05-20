package com.azure.spring.initializr.web.controller;

import com.azure.spring.initializr.autoconfigure.ExtendInitializrProperties;
import com.azure.spring.initializr.metadata.scm.push.OAuthApp;
import com.azure.spring.initializr.web.mapper.ExtendInitializrMetadataV21JsonMapper;
import com.azure.spring.initializr.web.mapper.ExtendInitializrMetadataV22JsonMapper;
import com.azure.spring.initializr.web.mapper.ExtendInitializrMetadataV2JsonMapper;
import io.spring.initializr.generator.version.Version;
import io.spring.initializr.metadata.DependencyMetadata;
import io.spring.initializr.metadata.DependencyMetadataProvider;
import io.spring.initializr.metadata.InitializrConfiguration;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.metadata.InvalidInitializrMetadataException;
import io.spring.initializr.web.controller.AbstractMetadataController;
import io.spring.initializr.web.mapper.DependencyMetadataV21JsonMapper;
import io.spring.initializr.web.mapper.InitializrMetadataJsonMapper;
import io.spring.initializr.web.mapper.InitializrMetadataVersion;
import io.spring.initializr.web.project.InvalidProjectRequestException;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class ExtendProjectMetadataController extends AbstractMetadataController {

    private final ExtendInitializrProperties properties;

    /**
     * HAL JSON content type.
     */
    public static final MediaType HAL_JSON_CONTENT_TYPE = MediaType.parseMediaType("application/hal+json");

    private final DependencyMetadataProvider dependencyMetadataProvider;

    public ExtendProjectMetadataController(InitializrMetadataProvider metadataProvider,
                                           DependencyMetadataProvider dependencyMetadataProvider,
                                           ExtendInitializrProperties properties) {
        super(metadataProvider);
        this.dependencyMetadataProvider = dependencyMetadataProvider;
        this.properties = properties;
    }

    @RequestMapping(path = "/metadata/oauthapps", produces = "application/json")
    @ResponseBody
    public Map<String, OAuthApp> oauthApps() {
        return this.properties.getOAuthApps();
    }

    @RequestMapping(path = "/metadata/config", produces = "application/json")
    @ResponseBody
    public InitializrMetadata config() {
        return this.metadataProvider.get();
    }

    @RequestMapping(path = { "/", "/metadata/client" }, produces = "application/hal+json")
    public ResponseEntity<String> serviceCapabilitiesHal() {
        return serviceCapabilitiesFor(InitializrMetadataVersion.V2_1, HAL_JSON_CONTENT_TYPE);
    }

    @RequestMapping(path = { "/", "/metadata/client" }, produces = { "application/vnd.initializr.v2.2+json" })
    public ResponseEntity<String> serviceCapabilitiesV22() {
        return serviceCapabilitiesFor(InitializrMetadataVersion.V2_2);
    }

    @RequestMapping(path = { "/", "/metadata/client" }, produces = { "application/vnd.initializr.v2.1+json",
        "application/json" })
    public ResponseEntity<String> serviceCapabilitiesV21() {
        return serviceCapabilitiesFor(InitializrMetadataVersion.V2_1);
    }

    @RequestMapping(path = { "/", "/metadata/client" }, produces = "application/vnd.initializr.v2+json")
    public ResponseEntity<String> serviceCapabilitiesV2() {
        return serviceCapabilitiesFor(InitializrMetadataVersion.V2);
    }

    @RequestMapping(path = "/dependencies", produces = "application/vnd.initializr.v2.2+json")
    public ResponseEntity<String> dependenciesV22(@RequestParam(required = false) String bootVersion) {
        return dependenciesFor(InitializrMetadataVersion.V2_2, bootVersion);
    }

    @RequestMapping(path = "/dependencies", produces = { "application/vnd.initializr.v2.1+json", "application/json" })
    public ResponseEntity<String> dependenciesV21(@RequestParam(required = false) String bootVersion) {
        return dependenciesFor(InitializrMetadataVersion.V2_1, bootVersion);
    }

    @ExceptionHandler
    public void invalidMetadataRequest(HttpServletResponse response, InvalidInitializrMetadataException ex) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    @ExceptionHandler
    public void invalidProjectRequest(HttpServletResponse response, InvalidProjectRequestException ex) throws IOException {
        response.sendError(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }

    /**
     * Return the {@link CacheControl} response headers to use for the specified {@link InitializrMetadata metadata}. If
     * no cache should be applied {@link CacheControl#empty()} can be used.
     *
     * @param metadata the metadata about to be exposed
     * @return the {@code Cache-Control} headers to use
     */
    protected CacheControl determineCacheControlFor(InitializrMetadata metadata) {
        return CacheControl.maxAge(2, TimeUnit.HOURS);
    }

    private ResponseEntity<String> dependenciesFor(InitializrMetadataVersion version, String bootVersion) {
        InitializrMetadata metadata = this.metadataProvider.get();
        Version v = (bootVersion != null) ? Version.parse(bootVersion) :
            Version.parse(metadata.getBootVersions().getDefault().getId());
        InitializrConfiguration.Platform platform = metadata.getConfiguration().getEnv().getPlatform();
        if (!platform.isCompatibleVersion(v)) {
            throw new InvalidProjectRequestException("Invalid Spring Boot version '" + bootVersion + "', Spring Boot "
                + "compatibility range is " + platform.determineCompatibilityRangeRequirement());
        }
        DependencyMetadata dependencyMetadata = this.dependencyMetadataProvider.get(metadata, v);
        String content = new DependencyMetadataV21JsonMapper().write(dependencyMetadata);
        return ResponseEntity.ok().contentType(version.getMediaType()).eTag(createUniqueId(content)).cacheControl(determineCacheControlFor(metadata)).body(content);
    }

    private ResponseEntity<String> serviceCapabilitiesFor(InitializrMetadataVersion version) {
        return serviceCapabilitiesFor(version, version.getMediaType());
    }

    private ResponseEntity<String> serviceCapabilitiesFor(InitializrMetadataVersion version, MediaType contentType) {
        String appUrl = generateAppUrl();
        InitializrMetadata metadata = this.metadataProvider.get();
        String content = getJsonMapper(version).write(metadata, appUrl);
        return ResponseEntity.ok().contentType(contentType).eTag(createUniqueId(content)).varyBy("Accept").cacheControl(determineCacheControlFor(metadata)).body(content);
    }

    private static InitializrMetadataJsonMapper getJsonMapper(InitializrMetadataVersion version) {
        switch (version) {
            case V2:
                return new ExtendInitializrMetadataV2JsonMapper();
            case V2_1:
                return new ExtendInitializrMetadataV21JsonMapper();
            default:
                return new ExtendInitializrMetadataV22JsonMapper();
        }
    }
}
