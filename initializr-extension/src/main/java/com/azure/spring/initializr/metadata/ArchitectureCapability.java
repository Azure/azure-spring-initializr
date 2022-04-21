package com.azure.spring.initializr.metadata;

import io.spring.initializr.metadata.DefaultMetadataElement;
import io.spring.initializr.metadata.Defaultable;
import io.spring.initializr.metadata.ServiceCapability;
import io.spring.initializr.metadata.ServiceCapabilityType;
import io.spring.initializr.metadata.Type;

import java.util.ArrayList;
import java.util.List;

public class ArchitectureCapability extends ServiceCapability<List<Architecture>> implements Defaultable<Architecture> {

    private final List<Architecture> content = new ArrayList<>();

    public ArchitectureCapability() {
        super("architecture", ServiceCapabilityType.SINGLE_SELECT, "Architecture", "Application Architecture");
    }

    @Override
    public List<Architecture> getContent() {
        return this.content;
    }

    /**
     * Return the {@link Type} with the specified id or {@code null} if no such type
     * exists.
     * @param id the ID to find
     * @return the Type or {@code null}
     */
    public Architecture get(String id) {
        return this.content.stream().filter((it) -> id.equals(it.getId())).findFirst().orElse(null);
    }

    /**
     * Return the default {@link Type}.
     */
    @Override
    public Architecture getDefault() {
        return this.content.stream().filter(DefaultMetadataElement::isDefault).findFirst().orElse(null);
    }

    @Override
    public void merge(List<Architecture> otherContent) {
        otherContent.forEach((it) -> {
            if (get(it.getId()) == null) {
                this.content.add(it);
            }
        });
    }
}
