package cn.kinkii.novice.framework.controller.request.annotations;

import cn.kinkii.novice.framework.controller.request.OperateLogType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestOperateLog {
    /**
     *  操作
     * @return
     */
    String operatorName() default "";

    /**
     *  操作类型
     * @return
     */
    OperateLogType operatorType() default OperateLogType.QUERY;

    /**
     *  操作描述
     * @return
     */
    String operatorDesc() default "";

}
