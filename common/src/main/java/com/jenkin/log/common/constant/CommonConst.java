package com.jenkin.log.common.constant;

import com.jenkin.log.common.utils.emailormsn.msgbeans.EmailUser;

/**
 * @author ：jenkin
 * @date ：Created at 2020/5/27 14:18
 * @description：
 * @modified By：
 * @version: 1.0
 */
public class CommonConst {
    /**
     * 邮件发送的默认用户
     */
    public static final EmailUser DEFAULT_EMAIL_USER = new EmailUser("1394046585@qq.com","grttrhcexizggcih","smtp.qq.com");

    public static final String HEARTBEAT_SUCCESS_MSG="200 OK heartbeat success !";
    public static final String HEARTBEAT_FAIL_MSG="500 ERROR heartbeat fail !";



}
