package de.dittwald.cinemap.repository.movie;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.Base64;

public class ByteArrayDeserializer extends StdDeserializer<Byte[]> {

    public ByteArrayDeserializer() {
        super(Byte[].class);
    }

    public ByteArrayDeserializer(Class<?> vc) {
        super(vc);
    }

    public ByteArrayDeserializer(JavaType valueType) {
        super(valueType);
    }

    public ByteArrayDeserializer(StdDeserializer<?> src) {
        super(src);
    }

    @Override
    public Byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        return ArrayUtils.toObject(Base64.getDecoder().decode(p.getText()));
    }
}
