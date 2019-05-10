package cn.kinkii.novice.framework.controller.query.annotations;

import cn.kinkii.novice.framework.controller.query.Expression;
import cn.kinkii.novice.framework.controller.query.Join;
import cn.kinkii.novice.framework.controller.query.Match;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryProperty {

    /**
     * 字段
     *
     * @return
     */
    String column();

    /**
     * 查询类型值
     *
     * @return
     */
    Expression expression() default Expression.EQ;


    Match match() default Match.ANYWHERE;

    /**
     * or关系时，关系字段
     *
     * @return
     */
    String group() default "";

    Join join() default Join.DEFAULT;

}
