package com.azure.spring.initializr.separation.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CodeGeneratorUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CodeGeneratorUtils.class);

    private static final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();


    public static void backendCodeGenerator(Path rootPath, List<String> locationPatterns) {
        Resource rootResource = resolver.getResource("classpath:web-backend");
        locationPatterns.parallelStream().forEach(locationPattern -> {
            try {
                Resource[] resources = resolver.getResources(locationPattern);
                Arrays.stream(resources).forEach(resource -> {
                    if (resource.isReadable()) {
                        try {
                            copyFile(rootPath, rootResource, resource);
                        } catch (IOException e) {
                            LOGGER.error(e.getMessage());
                        }
                    }
                });
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        });
    }

    public static void frontendCodeGenerator(Path rootPath, String artifactId, List<String> locationPatterns) {
        Resource rootResource = resolver.getResource("classpath:web-frontend");
        Path frontendPath = rootPath.getParent().resolve(artifactId + "-frontend");

        locationPatterns.parallelStream().forEach(locationPattern -> {
            try {
                Resource[] resources = resolver.getResources(locationPattern);
                Arrays.stream(resources).forEach(resource -> {
                    if (resource.isReadable()) {
                        try {
                            copyFile(frontendPath, rootResource, resource);
                        } catch (IOException e) {
                            LOGGER.error(e.getMessage());
                        }
                    }
                });
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        });
    }

    public static void copyFile(Path path, Resource root, Resource resource) throws IOException {
        String filename = extractFileName(root.getURI(), resource.getURI());
        Path output = path.resolve(filename);
        if (Files.notExists(output)) {
            Files.createDirectories(output.getParent());
            Files.createFile(output);
            FileCopyUtils.copy(resource.getInputStream(), Files.newOutputStream(output));
            //Files.copy(resource.getInputStream(), output, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static String extractFileName(URI root, URI resource) {
        String candidate = resource.toString().substring(root.toString().length());
        return StringUtils.trimLeadingCharacter(candidate, '/');
    }

    /**
     * 包名转换为包路径
     *
     * @param packageName 包名
     * @return
     */
    private static String packageToPath(String packageName) {
        String packagePath = packageName.replace(".", "/");
        return StringUtils.trimTrailingCharacter(packagePath, '/');
    }
}
