/*
 * Copyright 2012-2019 the original author or authors.
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

import io.spring.initializr.generator.spring.documentation.HelpDocument;
import io.spring.initializr.generator.spring.scm.git.GitIgnore;
import io.spring.initializr.generator.spring.scm.git.GitIgnoreCustomizer;

/**
 * A {@link GitIgnoreCustomizer} that removes {@code HELP.md} in the ignored list when
 * put the generated project to git repository.
 *
 */
public class ExtendHelpDocumentGitIgnoreCustomizer implements GitIgnoreCustomizer {

	private final HelpDocument document;

	public ExtendHelpDocumentGitIgnoreCustomizer(HelpDocument document) {
		this.document = document;
	}

	@Override
	public void customize(GitIgnore gitIgnore) {
		if (!this.document.isEmpty()) {
			gitIgnore.getGeneral().getItems().remove("HELP.md");
		}
	}

}
