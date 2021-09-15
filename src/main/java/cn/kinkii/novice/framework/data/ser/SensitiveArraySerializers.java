package cn.kinkii.novice.framework.data.ser;

import cn.kinkii.novice.framework.data.SensitiveDataUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import lombok.Getter;
import lombok.Setter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class SensitiveArraySerializers {

    public static final List<SensitiveArraySerializer<?>> SUPPORTED_SERIALIZERS = new ArrayList<>();

    static {
        SUPPORTED_SERIALIZERS.add(new SensitiveStringArraySerializer());
        SUPPORTED_SERIALIZERS.add(new SensitiveNumberArraySerializer());
        SUPPORTED_SERIALIZERS.add(new SensitiveShortArraySerializer());
        SUPPORTED_SERIALIZERS.add(new SensitiveIntArraySerializer());
        SUPPORTED_SERIALIZERS.add(new SensitiveLongArraySerializer());
        SUPPORTED_SERIALIZERS.add(new SensitiveFloatArraySerializer());
        SUPPORTED_SERIALIZERS.add(new SensitiveDoubleArraySerializer());
        SUPPORTED_SERIALIZERS.add(new SensitiveDateArraySerializer());
    }

    public static class SensitiveStringArraySerializer extends SensitiveArraySerializer<String[]> {

        public SensitiveStringArraySerializer() {
            super(String[].class);
        }

        @Override
        protected SensitiveArraySerializer<String[]> _createInstance(SerializerProvider provider, BeanProperty property) {
            return new SensitiveStringArraySerializer();
        }

        @Override
        public Object buildSensitiveData(String[] sensitiveDataValue, JsonGenerator jsonGenerator, SerializerProvider provider) {
            return Arrays.stream(sensitiveDataValue).map(s -> SensitiveDataUtils.sensitize(s, patterns, maskChar)).toArray();
        }

    }

    public static class SensitiveNumberArraySerializer extends SensitiveArraySerializer<Number[]> implements SensitiveNumberTypeSerializer {

        public SensitiveNumberArraySerializer() {
            super(Number[].class);
        }

        @Override
        public List<Class<?>> getUnsupportedNumberClasses() {
            return Collections.singletonList(Byte.class);
        }

        @Override
        public boolean isSupport(BeanProperty property) {
            return property.getType().isArrayType() &&
                    Number.class.isAssignableFrom(property.getType().getContentType().getRawClass()) &&
                    !getUnsupportedNumberClasses().contains(property.getType().getContentType().getRawClass());
        }

        @Override
        protected SensitiveArraySerializer<Number[]> _createInstance(SerializerProvider provider, BeanProperty property) {
            return new SensitiveNumberArraySerializer();
        }

        @Override
        public Object buildSensitiveData(Number[] sensitiveDataValue, JsonGenerator generator, SerializerProvider provider) throws JsonMappingException {
            String[] sensitizedValues = new String[sensitiveDataValue.length];
            for (int i = 0; i < sensitiveDataValue.length; i++) {
                sensitizedValues[i] = SensitiveDataUtils.sensitize(_valueToString(sensitiveDataValue[i], generator, provider), patterns, maskChar);
            }
            return sensitizedValues;
        }

    }

    public static class SensitiveShortArraySerializer extends SensitiveArraySerializer<short[]> {

        public SensitiveShortArraySerializer() {
            super(short[].class);
        }

        @Override
        protected SensitiveArraySerializer<short[]> _createInstance(SerializerProvider provider, BeanProperty property) {
            return new SensitiveShortArraySerializer();
        }

        @Override
        public Object buildSensitiveData(short[] sensitiveDataValue, JsonGenerator generator, SerializerProvider provider) throws JsonMappingException {
            String[] sensitizedValues = new String[sensitiveDataValue.length];
            for (int i = 0; i < sensitiveDataValue.length; i++) {
                sensitizedValues[i] = SensitiveDataUtils.sensitize(String.valueOf(sensitiveDataValue[i]), patterns, maskChar);
            }
            return sensitizedValues;
        }

    }

    public static class SensitiveIntArraySerializer extends SensitiveArraySerializer<int[]> {

        public SensitiveIntArraySerializer() {
            super(int[].class);
        }

        @Override
        protected SensitiveArraySerializer<int[]> _createInstance(SerializerProvider provider, BeanProperty property) {
            return new SensitiveIntArraySerializer();
        }

        @Override
        public Object buildSensitiveData(int[] sensitiveDataValue, JsonGenerator generator, SerializerProvider provider) throws JsonMappingException {
            String[] sensitizedValues = new String[sensitiveDataValue.length];
            for (int i = 0; i < sensitiveDataValue.length; i++) {
                sensitizedValues[i] = SensitiveDataUtils.sensitize(String.valueOf(sensitiveDataValue[i]), patterns, maskChar);
            }
            return sensitizedValues;
        }

    }

    public static class SensitiveLongArraySerializer extends SensitiveArraySerializer<long[]> {

        public SensitiveLongArraySerializer() {
            super(long[].class);
        }

        @Override
        protected SensitiveArraySerializer<long[]> _createInstance(SerializerProvider provider, BeanProperty property) {
            return new SensitiveLongArraySerializer();
        }

        @Override
        public Object buildSensitiveData(long[] sensitiveDataValue, JsonGenerator generator, SerializerProvider provider) throws JsonMappingException {
            String[] sensitizedValues = new String[sensitiveDataValue.length];
            for (int i = 0; i < sensitiveDataValue.length; i++) {
                sensitizedValues[i] = SensitiveDataUtils.sensitize(String.valueOf(sensitiveDataValue[i]), patterns, maskChar);
            }
            return sensitizedValues;
        }

    }

    public static class SensitiveFloatArraySerializer extends SensitiveArraySerializer<float[]> {

        public SensitiveFloatArraySerializer() {
            super(float[].class);
        }

        @Override
        protected SensitiveArraySerializer<float[]> _createInstance(SerializerProvider provider, BeanProperty property) {
            return new SensitiveFloatArraySerializer();
        }

        @Override
        public Object buildSensitiveData(float[] sensitiveDataValue, JsonGenerator generator, SerializerProvider provider) throws JsonMappingException {
            String[] sensitizedValues = new String[sensitiveDataValue.length];
            for (int i = 0; i < sensitiveDataValue.length; i++) {
                sensitizedValues[i] = SensitiveDataUtils.sensitize(String.valueOf(sensitiveDataValue[i]), patterns, maskChar);
            }
            return sensitizedValues;
        }

    }

    public static class SensitiveDoubleArraySerializer extends SensitiveArraySerializer<double[]> {

        public SensitiveDoubleArraySerializer() {
            super(double[].class);
        }

        @Override
        protected SensitiveArraySerializer<double[]> _createInstance(SerializerProvider provider, BeanProperty property) {
            return new SensitiveDoubleArraySerializer();
        }

        @Override
        public Object buildSensitiveData(double[] sensitiveDataValue, JsonGenerator generator, SerializerProvider provider) throws JsonMappingException {
            String[] sensitizedValues = new String[sensitiveDataValue.length];
            for (int i = 0; i < sensitiveDataValue.length; i++) {
                sensitizedValues[i] = SensitiveDataUtils.sensitize(String.valueOf(sensitiveDataValue[i]), patterns, maskChar);
            }
            return sensitizedValues;
        }

    }

    public static class SensitiveDateArraySerializer extends SensitiveArraySerializer<Date[]> implements SensitiveDateTypeSerializer {

        @Getter
        @Setter
        protected Boolean useTimestamp;

        @Getter
        @Setter
        protected DateFormat customFormat;

        public SensitiveDateArraySerializer() {
            super(Date[].class);
        }

        @Override
        protected boolean shouldUseDefault(SerializerProvider provider, BeanProperty property, String maskChar, List<Pattern> patterns) {
            JsonFormat.Value format = findFormatOverrides(provider, property, handledType());
            return super.shouldUseDefault(provider, property, maskChar, patterns) && (format == null || (!format.hasPattern() && !format.hasShape() && !format.hasTimeZone() && !format.hasLocale()));
        }

        @Override
        protected SensitiveArraySerializer<Date[]> _createInstance(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
            JsonFormat.Value format = findFormatOverrides(provider, property, handledType());
            SensitiveDateArraySerializer dateSerializer = new SensitiveDateArraySerializer();
            if (format != null) {
                JsonFormat.Shape shape = format.getShape();
                if (shape.isNumeric()) {
                    dateSerializer.setUseTimestamp(Boolean.TRUE);
                }
                if (format.hasPattern()) {
                    final Locale loc = format.hasLocale()
                            ? format.getLocale()
                            : provider.getLocale();
                    SimpleDateFormat df = new SimpleDateFormat(format.getPattern(), loc);
                    TimeZone tz = format.hasTimeZone() ? format.getTimeZone()
                            : provider.getTimeZone();
                    df.setTimeZone(tz);
                    dateSerializer.setCustomFormat(df);
                }
            }
            if (dateSerializer.getCustomFormat() == null) {
                DateFormat df0 = provider.getConfig().getDateFormat();
                if (df0 instanceof StdDateFormat) {
                    StdDateFormat std = (StdDateFormat) df0;
                    if (format != null) {
                        if (format.hasLocale()) {
                            std = std.withLocale(format.getLocale());
                        }
                        if (format.hasTimeZone()) {
                            std = std.withTimeZone(format.getTimeZone());
                        }
                    }
                    dateSerializer.setCustomFormat(std);
                } else if (df0 instanceof SimpleDateFormat) {
                    SimpleDateFormat df = (SimpleDateFormat) df0;
                    if (format != null && format.hasLocale()) {
                        df = new SimpleDateFormat(df.toPattern(), format.getLocale());
                    } else {
                        df = (SimpleDateFormat) df.clone();
                    }
                    TimeZone newTz = format != null ? format.getTimeZone() : null;
                    if ((newTz != null) && !newTz.equals(df.getTimeZone())) {
                        df.setTimeZone(newTz);
                    }
                    dateSerializer.setCustomFormat(df);
                } else {
                    provider.reportBadDefinition(handledType(), String.format(
                            "Configured `DateFormat` (%s) not a `SimpleDateFormat`; cannot configure `Locale` or `TimeZone`",
                            df0.getClass().getName()));
                }
            }
            dateSerializer.setMaskChar(maskChar);
            dateSerializer.setPatterns(patterns);

            return dateSerializer;
        }

        @Override
        public Object buildSensitiveData(Date[] sensitiveDataValue, JsonGenerator generator, SerializerProvider provider) throws JsonMappingException {
            String[] sensitizedValues = new String[sensitiveDataValue.length];
            if (_asTimestamp(provider)) {
                for (int i = 0; i < sensitiveDataValue.length; i++) {
                    sensitizedValues[i] = SensitiveDataUtils.sensitize(String.valueOf(_formatAsTimestamp(sensitiveDataValue[i])), patterns, maskChar);
                }
            } else {
                for (int i = 0; i < sensitiveDataValue.length; i++) {
                    sensitizedValues[i] = SensitiveDataUtils.sensitize(_formatAsString(sensitiveDataValue[i], provider), patterns, maskChar);
                }
            }
            return sensitizedValues;
        }

    }

}
