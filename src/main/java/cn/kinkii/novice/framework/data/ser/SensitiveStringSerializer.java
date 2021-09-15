package cn.kinkii.novice.framework.data.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import cn.kinkii.novice.framework.data.SensitiveDataUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class SensitiveStringSerializer extends ToStringSerializer implements SensitiveTypeSerializer<Object> {

    @Getter
    @Setter
    private String maskChar;

    @Getter
    @Setter
    private List<Pattern> patterns;

    @Override
    public boolean isSupport(BeanProperty property) {
        return Objects.equals(property.getType().getRawClass(), String.class);
    }

    @Override
    public JsonSerializer<Object> build(SerializerProvider provider, BeanProperty property, String maskChar, List<Pattern> patterns) throws JsonMappingException {
        if (this.isDefault(maskChar, patterns)) {
            return this;
        }
        SensitiveStringSerializer stringSerializer = new SensitiveStringSerializer();
        stringSerializer.setMaskChar(maskChar);
        stringSerializer.setPatterns(patterns);
        return stringSerializer;
    }

    @Override
    public void serialize(Object value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        write(value, generator, provider);
    }

    @Override
    public Object buildSensitiveData(Object sensitiveDataValue, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
        return SensitiveDataUtils.sensitize(valueToString(sensitiveDataValue), patterns, maskChar);
    }

}
