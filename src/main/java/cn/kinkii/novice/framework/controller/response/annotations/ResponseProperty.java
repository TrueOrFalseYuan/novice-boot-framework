package cn.kinkii.novice.framework.controller.response.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ResponseProperty {

  Class<?> sourceClass() default Object.class;

  String sourceProperty() default "";

}
