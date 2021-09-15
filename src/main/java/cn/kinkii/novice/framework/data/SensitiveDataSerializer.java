package cn.kinkii.novice.framework.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import cn.kinkii.novice.framework.data.ser.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
@Slf4j
public class SensitiveDataSerializer extends JsonSerializer<Object> implements ContextualSerializer {

    private final static List<SensitiveTypeSerializer> PRESET_SERIALIZERS = new ArrayList<>();

    static {
        PRESET_SERIALIZERS.add(new SensitiveDateSerializer());
        PRESET_SERIALIZERS.add(new SensitiveNumberSerializer());
        PRESET_SERIALIZERS.add(new SensitiveStringSerializer());
        PRESET_SERIALIZERS.addAll(SensitiveArraySerializers.SUPPORTED_SERIALIZERS);
        PRESET_SERIALIZERS.addAll(SensitiveCollectionSerializers.SUPPORTED_SERIALIZERS);
    }


    public static final String SENSITIVE_STATE_CHECKERS_KEY = "STATE_CHECKERS";
    public static final String SENSITIVE_ADDITIONAL_SERIALIZERS_KEY = "ADDITIONAL_SERIALIZERS";

    private final List<SensitiveStateChecker> stateCheckers;

    private final JsonSerializer originalSerializer;

    private final JsonSerializer sensitizeSerializer;

    @SuppressWarnings("unused")
    public SensitiveDataSerializer() {
        this(null, null, null);
    }

    public SensitiveDataSerializer(List<SensitiveStateChecker> checkers,
                                   JsonSerializer originalSerializer,
                                   JsonSerializer sensitizeSerializer) {
        this.stateCheckers = checkers;
        this.originalSerializer = originalSerializer;
        this.sensitizeSerializer = sensitizeSerializer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (this.stateCheckers != null && this.stateCheckers.size() > 0 && stateCheckers.stream().allMatch(SensitiveStateChecker::shouldDisable)) {
            originalSerializer.serialize(o, jsonGenerator, serializerProvider);
        } else if (sensitizeSerializer != null) {
            sensitizeSerializer.serialize(o, jsonGenerator, serializerProvider);
        } else {
            throw new IllegalStateException("Unsupported value to serialize! - " + o.getClass().getSimpleName());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
        if (property != null) {
            SensitiveData sdAnnotation = property.getAnnotation(SensitiveData.class);
            if (sdAnnotation == null) {
                sdAnnotation = property.getContextAnnotation(SensitiveData.class);
            }
            if (sdAnnotation != null) {
                List<SensitiveStateChecker> checkers = (List<SensitiveStateChecker>) provider.getAttribute(SENSITIVE_STATE_CHECKERS_KEY);
                List<SensitiveTypeSerializer> additionalSerializer = (List<SensitiveTypeSerializer>) provider.getAttribute(SENSITIVE_ADDITIONAL_SERIALIZERS_KEY);

                SensitiveTypeSerializer typeSerializer = null;
                List<Pattern> patterns = Arrays.stream(sdAnnotation.masks()).map(mask -> Pattern.compile(mask.matchRegex())).collect(Collectors.toList());
                if (additionalSerializer != null) {
                    typeSerializer = additionalSerializer.stream()
                            .filter(sensitiveTypeSerializer -> sensitiveTypeSerializer.isSupport(property))
                            .findFirst()
                            .orElse(null);
                }
                if (typeSerializer == null) {
                    typeSerializer = PRESET_SERIALIZERS.stream()
                            .filter(sensitiveTypeSerializer -> sensitiveTypeSerializer.isSupport(property))
                            .findFirst()
                            .orElse(null);
                }
                if (typeSerializer != null) {
                    return new SensitiveDataSerializer(checkers,
                            provider.findValueSerializer(property.getType(), property),
                            typeSerializer.build(provider, property, sdAnnotation.maskChar(), patterns));
                }
            }
            return provider.findValueSerializer(property.getType(), property);
        }
        return provider.findNullValueSerializer(null);
    }

}