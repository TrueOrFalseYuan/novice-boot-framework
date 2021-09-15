package cn.kinkii.novice.framework.data.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public interface SensitiveTypeSerializer<T> {

    String SENSITIVE_DATA_MASKED_KEY = "masked";
    String SENSITIVE_DATA_VALUE_KEY = "value";

    String DEFAULT_MASK_CHAR = "*";

    boolean isSupport(BeanProperty property);

    default boolean isDefault(String maskChar, List<Pattern> patterns) {
        return SensitiveTypeSerializer.DEFAULT_MASK_CHAR.equals(maskChar) && (patterns == null || patterns.size() == 0);
    }

    JsonSerializer<T> build(SerializerProvider provider, BeanProperty property, String maskChar, List<Pattern> patterns) throws JsonMappingException;

    default void write(T value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeStartObject();
        generator.writeBooleanField(SENSITIVE_DATA_MASKED_KEY, true);
        Object sensitizedValue = this.buildSensitiveData(value, generator, provider);
        if (sensitizedValue != null) {
            generator.writeObjectField(SensitiveTypeSerializer.SENSITIVE_DATA_VALUE_KEY, sensitizedValue);
        }
        generator.writeEndObject();
    }

    Object buildSensitiveData(T sensitiveDataValue, JsonGenerator jsonGenerator, SerializerProvider provider) throws JsonMappingException;

}
