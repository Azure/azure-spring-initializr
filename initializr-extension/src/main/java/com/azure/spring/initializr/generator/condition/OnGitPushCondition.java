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

package com.azure.spring.initializr.generator.condition;

import com.azure.spring.initializr.extension.scm.push.common.GitServiceEnum;
import com.azure.spring.initializr.generator.project.ExtendProjectDescription;
import io.spring.initializr.generator.condition.ProjectGenerationCondition;
import io.spring.initializr.generator.project.ProjectDescription;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * {@link ProjectGenerationCondition Condition} implementation for
 * {@link ConditionalOnGitPush}.
 *
 */
class OnGitPushCondition extends ProjectGenerationCondition {

	@Override
	protected boolean matches(ProjectDescription description, ConditionContext context,
			AnnotatedTypeMetadata metadata) {
		if (description instanceof ExtendProjectDescription) {
			String gitServiceType = ((ExtendProjectDescription) description).getGitServiceType();
			for (GitServiceEnum value : GitServiceEnum.values()) {
				if (value.getName().equals(gitServiceType)) {
					return true;
				}
			}
		}
		return false;
	}
}
