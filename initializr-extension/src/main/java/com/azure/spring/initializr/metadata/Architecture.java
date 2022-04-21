package com.azure.spring.initializr.metadata;

import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.Describable;

import java.util.ArrayList;
import java.util.List;

public class Architecture extends DefaultMetadataElement implements Describable {
    private String description;
    private List<String> facets = new ArrayList<>();

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getFacets() {
        return facets;
    }

    public void setFacets(List<String> facets) {
        this.facets = facets;
    }
}
