package cn.kinkii.novice.framework.data.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface SensitiveNumberTypeSerializer {

    int MAX_BIG_DECIMAL_SCALE = 9999;

    default List<Class<?>> getUnsupportedNumberClasses() {
        return Collections.emptyList();
    }

    default List<Class<?>> getUnsupportedPrimitiveNumberClasses() {
        return Arrays.asList(boolean.class, char.class);
    }

    default String _valueToString(Object value, JsonGenerator generator, SerializerProvider provider) throws JsonMappingException {
        if (Number.class.isAssignableFrom(value.getClass())) {
            if (getUnsupportedNumberClasses().contains(value.getClass())) {
                provider.reportMappingProblem("Attempt to sensitize unsupported Number type - " + value.getClass().getName());
            }
            if (value instanceof BigDecimal && generator.isEnabled(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN)) {
                final BigDecimal bd = (BigDecimal) value;
                if (_illegalBigDecimalRange(bd)) {
                    final String errorMsg = String.format(
                            "Attempt to write plain `java.math.BigDecimal` (see JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN) with illegal scale (%d): needs to be between [-%d, %d]",
                            bd.scale(), MAX_BIG_DECIMAL_SCALE, MAX_BIG_DECIMAL_SCALE);
                    provider.reportMappingProblem(errorMsg);
                }
                return bd.toPlainString();
            } else {
                return value.toString();
            }
        } else {
            provider.reportMappingProblem("Unsupported type to serialize by SensitiveNumberTypeSerializer - " + value.getClass().getName());
        }
        return "";
    }

    default boolean _illegalBigDecimalRange(BigDecimal value) {
        return (value.scale() < -MAX_BIG_DECIMAL_SCALE) || (value.scale() > MAX_BIG_DECIMAL_SCALE);
    }

}
