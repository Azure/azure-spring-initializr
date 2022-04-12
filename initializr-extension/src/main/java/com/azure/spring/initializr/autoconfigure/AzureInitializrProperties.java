package com.azure.spring.initializr.autoconfigure;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Files;
import java.nio.file.Path;

@ConfigurationProperties(prefix = "azure.initializr")
public class AzureInitializrProperties implements InitializingBean {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(AzureInitializrProperties.class);

	private Path mavenResolverCacheDirectory;



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
		if (!Files.exists(mavenResolverCacheDirectory)) {
			LOGGER.info("Creating Maven resolver cache directory: {}", mavenResolverCacheDirectory);
			Files.createDirectory(mavenResolverCacheDirectory);
		}
		LOGGER.info("Maven resolver cache directory: {}", mavenResolverCacheDirectory);
	}

}
