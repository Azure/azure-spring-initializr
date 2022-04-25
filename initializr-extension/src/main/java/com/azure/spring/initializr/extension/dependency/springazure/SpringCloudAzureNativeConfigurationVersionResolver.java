/*
 * Copyright 2012-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.azure.spring.initializr.extension.dependency.springazure;

import io.spring.initializr.generator.version.Version;
import io.spring.initializr.generator.version.VersionParser;
import io.spring.initializr.generator.version.VersionRange;

import java.util.Arrays;
import java.util.List;

/**
 * Resolve the Spring Cloud Azure Native Configuration version to use based on the Spring Boot version.
 *
 */
abstract class SpringCloudAzureNativeConfigurationVersionResolver {

	private static final List<NativeConfigurationRange> ranges = Arrays.asList(
        new NativeConfigurationRange("[2.6.4, 2.6.7)", "4.0.0-beta.1"));

	static String resolve(String springBootVersion) {
		Version nativeVersion = VersionParser.DEFAULT.parse(springBootVersion);
		return ranges.stream().filter((range) -> range.match(nativeVersion)).findFirst()
                     .map(NativeConfigurationRange::getVersion).orElse(null);
	}

	private static class NativeConfigurationRange {

		private final VersionRange range;

		private final String version;

        NativeConfigurationRange(String range, String version) {
			this.range = parseRange(range);
			this.version = version;
		}

		String getVersion() {
			return this.version;
		}

		private static VersionRange parseRange(String s) {
			return VersionParser.DEFAULT.parseRange(s);
		}

		boolean match(Version version) {
			return this.range.match(version);
		}

	}

}
