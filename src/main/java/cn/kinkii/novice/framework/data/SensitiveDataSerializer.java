package cn.kinkii.novice.framework.data;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class SensitiveDataSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private static final String REPLACE_ALL_REGEX = "(.)";

    private final String maskChar;

    private final List<Pattern> matchPatternList;

    @SuppressWarnings("unused")
    public SensitiveDataSerializer() {
        this("*", new ArrayList<>());
    }

    public SensitiveDataSerializer(String maskChar, List<String> matchRegexList) {
        this.maskChar = maskChar;
        this.matchPatternList = matchRegexList.stream().map(Pattern::compile).collect(Collectors.toList());
    }

    @Override
    public void serialize(String s, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String maskedValue = s;
        if (this.matchPatternList != null && this.matchPatternList.size() > 0) {
            for (Pattern p : this.matchPatternList) {
                Matcher m = p.matcher(s);
                while (m.find()) {
                    for (int i = 1; i <= m.groupCount(); i++) {
                        maskedValue = maskedValue.replace(s.subSequence(m.start(i), m.end(i)), String.join("", Collections.nCopies(m.end(i) - m.start(i), "*")));
                    }
                }
            }
        } else {
            maskedValue = maskedValue.replaceAll(REPLACE_ALL_REGEX, this.maskChar);
        }
        writeMaskedData(jsonGenerator, maskedValue);
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
                SensitiveData sdAnnotation = beanProperty.getAnnotation(SensitiveData.class);
                if (sdAnnotation == null) {
                    sdAnnotation = beanProperty.getContextAnnotation(SensitiveData.class);
                }
                if (sdAnnotation != null) {
                    return new SensitiveDataSerializer(
                            sdAnnotation.maskChar(),
                            Arrays.stream(sdAnnotation.masks()).map(SensitiveDataMask::matchRegex).collect(Collectors.toList())
                    );
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(null);
    }

    private static void writeMaskedData(JsonGenerator jsonGenerator, String maskedValue) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeBooleanField("masked", true);
        jsonGenerator.writeStringField("value", maskedValue);
        jsonGenerator.writeEndObject();
    }

}
