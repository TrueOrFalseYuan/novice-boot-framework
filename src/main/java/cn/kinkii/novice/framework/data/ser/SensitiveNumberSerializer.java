package cn.kinkii.novice.framework.data.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class SensitiveNumberSerializer extends SensitiveStringSerializer implements SensitiveNumberTypeSerializer {

    @Override
    public boolean isSupport(BeanProperty property) {
        return Number.class.isAssignableFrom(property.getType().getRawClass()) ||
                (property.getType().getRawClass().isPrimitive() && !getUnsupportedPrimitiveNumberClasses().contains(property.getType().getRawClass()));
    }

    @Override
    public JsonSerializer<Object> build(SerializerProvider provider, BeanProperty property, String maskChar, List<Pattern> patterns) throws JsonMappingException {
        if (this.isDefault(maskChar, patterns)) {
            return this;
        }
        SensitiveNumberSerializer numberSerializer = new SensitiveNumberSerializer();
        numberSerializer.setMaskChar(maskChar);
        numberSerializer.setPatterns(patterns);
        return numberSerializer;
    }

    @Override
    public void serialize(Object value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        super.serialize(_valueToString(value, generator, provider), generator, provider);
    }

}
