package com.azure.spring.initializr.autoconfigure;


import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.spring.initializr.generator.version.VersionProperty;

/**
 * A {@link SimpleModule} that registers custom serializers.
 *
 * @author Stephane Nicoll
 */
class InitializrModule extends SimpleModule {

    InitializrModule() {
        super("initializr");
        addSerializer(new InitializrModule.VersionPropertySerializer());
    }

    private static class VersionPropertySerializer extends StdSerializer<VersionProperty> {

        VersionPropertySerializer() {
            super(VersionProperty.class);
        }

        @Override
        public void serialize(VersionProperty value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
            gen.writeString(value.toStandardFormat());
        }

    }

}
