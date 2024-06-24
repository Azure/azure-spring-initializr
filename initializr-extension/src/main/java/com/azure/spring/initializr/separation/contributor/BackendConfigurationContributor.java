package com.azure.spring.initializr.separation.contributor;


import com.azure.spring.initializr.separation.util.CodeGeneratorUtils;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.io.template.TemplateRenderer;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class BackendConfigurationContributor implements ProjectContributor {

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    private final ProjectDescription description;

    private final TemplateRenderer templateRenderer;


    public BackendConfigurationContributor(ProjectDescription description, TemplateRenderer templateRenderer) {
        this.description = description;
        this.templateRenderer = templateRenderer;
    }

    @Override
    public void contribute(Path projectRoot) throws IOException {
        Resource root = this.resolver.getResource("classpath:web-backend");
        Resource resource = this.resolver.getResource("classpath:web-backend/src/main/resources/application.yml");
        if (resource.isReadable()) {
            CodeGeneratorUtils.copyFile(projectRoot, root, resource);
        }
        Map<String, Object> tempMap = new HashMap<>();
        for (Map.Entry<String, Dependency> entry : description.getRequestedDependencies().entrySet()) {
            tempMap.put(entry.getKey(), entry.getValue());
        }

        Files.createDirectories(projectRoot.resolve("src/main/resources/"));
        Path codeFileDev = Files.createFile(projectRoot.resolve("src/main/resources/application-dev.yml"));
        Path codeFileTest = Files.createFile(projectRoot.resolve("src/main/resources/application-test.yml"));
        Path codeFileProd = Files.createFile(projectRoot.resolve("src/main/resources/application-prod.yml"));
        String code = this.templateRenderer.render("application.yml", tempMap);

        try (PrintWriter codeWriterDev = new PrintWriter(Files.newBufferedWriter(codeFileDev))) {
            codeWriterDev.print(code);
        }
        try (PrintWriter codeWriterTest = new PrintWriter(Files.newBufferedWriter(codeFileTest))) {
            codeWriterTest.print(code);
        }
        try (PrintWriter codeWriterProd = new PrintWriter(Files.newBufferedWriter(codeFileProd))) {
            codeWriterProd.print(code);
        }
    }
}
