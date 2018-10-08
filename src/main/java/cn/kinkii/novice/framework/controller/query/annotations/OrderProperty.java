package cn.kinkii.novice.framework.controller.query.annotations;

import cn.kinkii.novice.framework.controller.query.Direction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OrderProperty {

    String column();

    Direction direction() default Direction.DESC;

}
