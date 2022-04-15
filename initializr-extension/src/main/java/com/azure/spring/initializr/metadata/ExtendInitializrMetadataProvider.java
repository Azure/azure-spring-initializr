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

package com.azure.spring.initializr.metadata;

import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.support.InitializrMetadataUpdateStrategy;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.Cacheable;

public class ExtendInitializrMetadataProvider implements InitializrMetadataProvider {
    private InitializrMetadata metadata;

    private final ObjectProvider<InitializrMetadataUpdateStrategy> strategies;

    public ExtendInitializrMetadataProvider(InitializrMetadata metadata,
                                            ObjectProvider<InitializrMetadataUpdateStrategy> strategies) {
        this.metadata = metadata;
        this.strategies = strategies;
    }

    @Override
    @Cacheable(value = "initializr.metadata", key = "'metadata'")
    public InitializrMetadata get() {
        this.strategies.orderedStream().forEach(strategy -> {
            this.metadata = strategy.update(this.metadata);
        });
        return this.metadata;
    }
}
