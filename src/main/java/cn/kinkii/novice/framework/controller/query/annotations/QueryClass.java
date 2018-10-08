package cn.kinkii.novice.framework.controller.query.annotations;

import cn.kinkii.novice.framework.controller.query.Junction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryClass {

    Junction junction() default Junction.AND;

    OrderProperty[] orders();

}
