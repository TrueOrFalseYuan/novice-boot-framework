package cn.kinkii.novice.framework.data.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public abstract class SensitiveArraySerializer<T> extends StdSerializer<T> implements SensitiveTypeSerializer<T> {

    @Getter
    @Setter
    protected Boolean unwrapSingle;

    @Getter
    @Setter
    protected String maskChar;

    @Getter
    @Setter
    protected List<Pattern> patterns;

    protected SensitiveArraySerializer(Class<?> t) {
        super(t, false);
    }

    @Override
    public boolean isSupport(BeanProperty property) {
        return property.getType().isArrayType() && Objects.equals(property.getType().getRawClass(), this._handledType);
    }

    protected boolean shouldUseDefault(SerializerProvider provider, BeanProperty property, String maskChar, List<Pattern> patterns) {
        return isDefault(maskChar, patterns) && findFormatFeature(provider, property, _handledType, JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED) != null;
    }

    @Override
    public JsonSerializer<T> build(SerializerProvider provider, BeanProperty property, String maskChar, List<Pattern> patterns) throws JsonMappingException {
        if (shouldUseDefault(provider, property, maskChar, patterns)) {
            return this;
        }
        SensitiveArraySerializer<T> arraySerializer = this._createInstance(provider, property);
        arraySerializer.setUnwrapSingle(findFormatFeature(provider, property, _handledType, JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED));
        arraySerializer.setMaskChar(maskChar);
        arraySerializer.setPatterns(patterns);
        return arraySerializer;
    }

    @Override
    public void serialize(T value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        write(value, generator, provider);
    }

    protected abstract SensitiveArraySerializer<T> _createInstance(SerializerProvider provider, BeanProperty property) throws JsonMappingException;

}
