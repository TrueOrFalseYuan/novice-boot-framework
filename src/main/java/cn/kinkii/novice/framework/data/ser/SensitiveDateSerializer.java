package cn.kinkii.novice.framework.data.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import cn.kinkii.novice.framework.data.SensitiveDataUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class SensitiveDateSerializer extends StdSerializer<Date> implements SensitiveTypeSerializer<Date>, SensitiveDateTypeSerializer {

    @Getter
    @Setter
    protected Boolean useTimestamp;

    @Getter
    @Setter
    protected DateFormat customFormat;

    @Getter
    @Setter
    protected String maskChar;

    @Getter
    @Setter
    protected List<Pattern> patterns;

    public SensitiveDateSerializer() {
        super(Date.class);
    }

    @Override
    public boolean isSupport(BeanProperty property) {
        return Objects.equals(property.getType().getRawClass(), Date.class);
    }

    protected boolean shouldUseDefault(SerializerProvider provider, BeanProperty property, String maskChar, List<Pattern> patterns) {
        JsonFormat.Value format = findFormatOverrides(provider, property, handledType());
        return isDefault(maskChar, patterns) && (format == null || (!format.hasPattern() && !format.hasShape() && !format.hasTimeZone() && !format.hasLocale()));
    }

    @Override
    public JsonSerializer<Date> build(SerializerProvider provider, BeanProperty property, String maskChar, List<Pattern> patterns) throws JsonMappingException {
        if (shouldUseDefault(provider, property, maskChar, patterns)) {
            return this;
        }
        JsonFormat.Value format = findFormatOverrides(provider, property, handledType());
        SensitiveDateSerializer dateSerializer = new SensitiveDateSerializer();
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
    public void serialize(Date value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        write(value, generator, provider);
    }

    @Override
    public Object buildSensitiveData(Date value, JsonGenerator generator, SerializerProvider provider) {
        if (_asTimestamp(provider)) {
            return SensitiveDataUtils.sensitize(String.valueOf(_formatAsTimestamp(value)), patterns, maskChar);
        }
        return SensitiveDataUtils.sensitize(_formatAsString(value, provider), patterns, maskChar);
    }

}
