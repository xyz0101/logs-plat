package com.definesys.log.common.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OrderAndName {
    /**
     * 名称，code，做唯一标识
     * @return
     */
    String name();

    /**
     * 备注们勇于前台展示
     * @return
     */
    String note() default "";

    /**
     * 序号，可排序
     * @return
     */
    int order();

    /**
     * 前台是否可选
     * @return
     */
    boolean show() default true;

}
