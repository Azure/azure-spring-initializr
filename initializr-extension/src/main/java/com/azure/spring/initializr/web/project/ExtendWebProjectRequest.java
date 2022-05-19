package com.azure.spring.initializr.web.project;

import io.spring.initializr.metadata.InitializrMetadata;
import org.springframework.beans.BeanWrapperImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExtendWebProjectRequest extends ExtendProjectRequest {
    private String architecture;
    private List<String> samples = new ArrayList<>();

    private final Map<String, Object> parameters = new LinkedHashMap<>();

    /**
     * Return the additional parameters that can be used to further identify the request.
     * @return the parameters
     */
    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public List<String> getSamples() {
        return samples;
    }

    public void setSamples(List<String> samples) {
        this.samples = samples;
    }

    /**
     * Initialize the state of this request with defaults defined in the
     * {@link InitializrMetadata metadata}.
     * @param metadata the metadata to use
     */
    public void initialize(InitializrMetadata metadata) {
        BeanWrapperImpl bean = new BeanWrapperImpl(this);
        metadata.defaults().forEach((key, value) -> {
            if (bean.isWritableProperty(key)) {
                // We want to be able to infer a package name if none has been
                // explicitly set
                if (!key.equals("packageName")) {
                    bean.setPropertyValue(key, value);
                }
            }
        });
    }
}
