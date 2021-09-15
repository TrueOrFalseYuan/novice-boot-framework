package cn.kinkii.novice.framework.data;

import cn.kinkii.novice.framework.data.ser.SensitiveTypeSerializer;
import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveDataSerializer.class)
public @interface SensitiveData {

    SensitiveDataMask[] masks() default {};

    String maskChar() default SensitiveTypeSerializer.DEFAULT_MASK_CHAR;

}
