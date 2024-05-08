package de.dittwald.cinemap.repository.movie;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.Base64;

public class ByteArraySerializer extends StdSerializer<Byte[]> {

    public ByteArraySerializer() {
        super(Byte[].class);
    }

    protected ByteArraySerializer(Class<Byte[]> t) {
        super(t);
    }

    protected ByteArraySerializer(JavaType type) {
        super(type);
    }

    protected ByteArraySerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    protected ByteArraySerializer(StdSerializer<?> src) {
        super(src);
    }

    @Override
    public void serialize(Byte[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(new String(Base64.getEncoder().encode(ArrayUtils.toPrimitive(value))));
    }
}
