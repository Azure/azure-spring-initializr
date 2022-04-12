package com.azure.spring.initializr.autoconfigure;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Files;
import java.nio.file.Path;

@ConfigurationProperties(prefix = "azure.initializr")
public class AzureInitializrProperties implements InitializingBean {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AzureInitializrProperties.class);

	private Path projectDirectory;

	private Path mavenResolverCacheDirectory;

	public Path getProjectDirectory() {
		return projectDirectory;
	}

	public void setProjectDirectory(Path projectDirectory) {
		this.projectDirectory = projectDirectory;
	}

	public Path getMavenResolverCacheDirectory() {
		return mavenResolverCacheDirectory;
	}

	public void setMavenResolverCacheDirectory(Path mavenResolverCacheDirectory) {
		this.mavenResolverCacheDirectory = mavenResolverCacheDirectory;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (mavenResolverCacheDirectory == null) {
			mavenResolverCacheDirectory = Files.createTempDirectory("version-resolver-cache-");
		}
		if (projectDirectory == null) {
			projectDirectory = Files.createTempDirectory("project-");
		}
		if (!Files.exists(mavenResolverCacheDirectory)) {
			LOGGER.info("Creating Maven resolver cache directory: {}", mavenResolverCacheDirectory);
			Files.createDirectory(mavenResolverCacheDirectory);
		}
		if (!Files.exists(projectDirectory)) {
			LOGGER.info("Creating project directory: {}", projectDirectory);
			Files.createDirectory(projectDirectory);
		}
		LOGGER.info("Project directory: {}", projectDirectory);
		LOGGER.info("Maven resolver cache directory: {}", mavenResolverCacheDirectory);
	}

}
