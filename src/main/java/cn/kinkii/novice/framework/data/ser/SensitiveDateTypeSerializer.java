package cn.kinkii.novice.framework.data.ser;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.text.DateFormat;
import java.util.Date;

public interface SensitiveDateTypeSerializer {

    Boolean getUseTimestamp();

    DateFormat getCustomFormat();

    default boolean _asTimestamp(SerializerProvider serializers) {
        if (getUseTimestamp() != null) {
            return getUseTimestamp();
        }
        if (getCustomFormat() == null) {
            if (serializers != null) {
                return serializers.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            }
        }
        return false;
    }

    default long _formatAsTimestamp(Date value) {
        return (value == null) ? 0L : value.getTime();
    }

    default String _formatAsString(Date value, SerializerProvider provider) {
        String formattedDate;
        if (getCustomFormat() == null) {
            formattedDate = provider.getConfig().getDateFormat().format(value);
        } else {
            formattedDate = getCustomFormat().format(value);
        }
        return formattedDate;
    }
}
