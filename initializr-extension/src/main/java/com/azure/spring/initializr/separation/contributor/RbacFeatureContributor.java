package com.azure.spring.initializr.separation.contributor;

import com.azure.spring.initializr.separation.util.CodeGeneratorUtils;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RbacFeatureContributor implements ProjectContributor {

    private final ProjectDescription description;

    public RbacFeatureContributor(ProjectDescription description) {
        this.description = description;
    }


    @Override
    public void contribute(Path projectRoot) throws IOException {
        String artifactId = description.getArtifactId();

        List<String> backendLocationPatterns = new ArrayList<>();
        backendLocationPatterns.add("classpath:web-backend/src/main/java/com/example/demo/config/GlobalExceptionHandler.java");
        backendLocationPatterns.add("classpath:web-backend/src/main/java/com/example/demo/config/WebMvcConfig.java");

        backendLocationPatterns.add("classpath:web-backend/src/main/java/com/example/demo/sys/user/config/**");
        backendLocationPatterns.add("classpath:web-backend/src/main/java/com/example/demo/sys/user/controller/**");
        backendLocationPatterns.add("classpath:web-backend/src/main/java/com/example/demo/sys/user/dto/**");
        backendLocationPatterns.add("classpath:web-backend/src/main/java/com/example/demo/sys/user/entity/**");
        backendLocationPatterns.add("classpath:web-backend/src/main/java/com/example/demo/sys/user/mapper/**");
        backendLocationPatterns.add("classpath:web-backend/src/main/java/com/example/demo/sys/user/param/**");
        backendLocationPatterns.add("classpath:web-backend/src/main/java/com/example/demo/sys/user/service/**");

        backendLocationPatterns.add("classpath:web-backend/src/main/resources/sql/UserMapper.xml");

        backendLocationPatterns.add("classpath:web-backend/src/main/resources/ddl/mysql/sys_rbac.sql");
        backendLocationPatterns.add("classpath:web-backend/src/main/resources/ddl/mysql/sys_delegator.sql");

        backendLocationPatterns.add("classpath:web-backend/src/main/resources/i18n/**");
        backendLocationPatterns.add("classpath:web-backend/README.md");
        CodeGeneratorUtils.backendCodeGenerator(projectRoot, backendLocationPatterns);

        List<String> frontendLocationPatterns = new ArrayList<>();
        frontendLocationPatterns.add("classpath:web-frontend/src/api/system/rbac.js");
        frontendLocationPatterns.add("classpath:web-frontend/src/mock/system/rbac.js");
        frontendLocationPatterns.add("classpath:web-frontend/src/views/system/rbac/**");
        CodeGeneratorUtils.frontendCodeGenerator(projectRoot, artifactId, frontendLocationPatterns);
    }
}