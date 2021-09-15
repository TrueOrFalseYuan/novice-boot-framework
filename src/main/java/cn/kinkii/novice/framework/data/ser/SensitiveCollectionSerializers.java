package cn.kinkii.novice.framework.data.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import cn.kinkii.novice.framework.data.SensitiveDataUtils;
import lombok.Getter;
import lombok.Setter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class SensitiveCollectionSerializers {

    public static final List<SensitiveCollectionSerializer<?>> SUPPORTED_SERIALIZERS = new ArrayList<>();

    static {
        SUPPORTED_SERIALIZERS.add(new SensitiveStringCollectionSerializer());
        SUPPORTED_SERIALIZERS.add(new SensitiveNumberCollectionSerializer());
        SUPPORTED_SERIALIZERS.add(new SensitiveDateCollectionSerializer());
    }

    public static class SensitiveStringCollectionSerializer extends SensitiveCollectionSerializer<String> {

        protected SensitiveStringCollectionSerializer() {
            super(String.class);
        }

        @Override
        protected SensitiveArraySerializer<Collection<String>> _createInstance(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
            return new SensitiveStringCollectionSerializer();
        }

        @Override
        public Object buildSensitiveData(Collection<String> sensitiveDataValue, JsonGenerator jsonGenerator, SerializerProvider provider) throws JsonMappingException {
            return sensitiveDataValue.stream().map(s -> SensitiveDataUtils.sensitize(s, patterns, maskChar)).toArray();
        }

    }

    public static class SensitiveNumberCollectionSerializer extends SensitiveCollectionSerializer<Number> implements SensitiveNumberTypeSerializer {

        public SensitiveNumberCollectionSerializer() {
            super(Number.class);
        }

        @Override
        public boolean isSupport(BeanProperty property) {
            return property.getType().isCollectionLikeType() && Number.class.isAssignableFrom(property.getType().getContentType().getRawClass());
        }

        @Override
        protected SensitiveArraySerializer<Collection<Number>> _createInstance(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
            return new SensitiveNumberCollectionSerializer();
        }

        @Override
        public Object buildSensitiveData(Collection<Number> sensitiveDataValue, JsonGenerator generator, SerializerProvider provider) throws JsonMappingException {
            String[] sensitizedValues = new String[sensitiveDataValue.size()];
            Iterator<Number> iterator = sensitiveDataValue.iterator();
            int i = 0;
            while (iterator.hasNext()) {
                sensitizedValues[i++] = SensitiveDataUtils.sensitize(_valueToString(iterator.next(), generator, provider), patterns, maskChar);
            }
            return sensitizedValues;
        }

    }

    public static class SensitiveDateCollectionSerializer extends SensitiveCollectionSerializer<Date> implements SensitiveDateTypeSerializer {

        @Getter
        @Setter
        protected Boolean useTimestamp;

        @Getter
        @Setter
        protected DateFormat customFormat;

        public SensitiveDateCollectionSerializer() {
            super(Date.class);
        }

        @Override
        protected boolean shouldUseDefault(SerializerProvider provider, BeanProperty property, String maskChar, List<Pattern> patterns) {
            JsonFormat.Value format = findFormatOverrides(provider, property, handledType());
            return super.shouldUseDefault(provider, property, maskChar, patterns) && (format == null || (!format.hasPattern() && !format.hasShape() && !format.hasTimeZone() && !format.hasLocale()));
        }

        @Override
        protected SensitiveArraySerializer<Collection<Date>> _createInstance(SerializerProvider provider, BeanProperty property) throws JsonMappingException {
            JsonFormat.Value format = findFormatOverrides(provider, property, handledType());
            SensitiveDateCollectionSerializer dateSerializer = new SensitiveDateCollectionSerializer();
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
        public Object buildSensitiveData(Collection<Date> sensitiveDataValue, JsonGenerator generator, SerializerProvider provider) throws JsonMappingException {
            String[] sensitizedValues = new String[sensitiveDataValue.size()];
            Iterator<Date> iterator = sensitiveDataValue.iterator();
            int i = 0;
            if (_asTimestamp(provider)) {
                while (iterator.hasNext()) {
                    sensitizedValues[i++] = SensitiveDataUtils.sensitize(String.valueOf(_formatAsTimestamp(iterator.next())), patterns, maskChar);
                }
            } else {
                while (iterator.hasNext()) {
                    sensitizedValues[i++] = SensitiveDataUtils.sensitize(_formatAsString(iterator.next(), provider), patterns, maskChar);
                }
            }
            return sensitizedValues;
        }

    }
}
