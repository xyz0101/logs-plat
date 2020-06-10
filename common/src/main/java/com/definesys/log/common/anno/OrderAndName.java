package com.definesys.log.common.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标记processer的顺序和名称等等
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OrderAndName {
    /**
     * 名称，code，做唯一标识
     * @return
     */
    String name();

    /**
     * 备注用于前台展示,比如email 的note为邮件发送服务，那么前台就会展示为邮件发送服务
     * @return
     */
    String note() default "";

    /**
     * 序号，可排序，可以定义当前这个processer的优先级
     * @return
     */
    int order();

    /**
     * 前台是否可选，如果为false前台不可选，那么前台是看不到这个processer的信息的，适用于一些需要默认处理的processer，比如最后的保存到es
     * @return
     */
    boolean show() default true;

}
