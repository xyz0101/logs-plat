package com.definesys.log.common.utils.emailormsn.email;


import com.definesys.log.common.utils.emailormsn.msgbeans.EmailMsg;
import com.definesys.log.common.utils.emailormsn.msgbeans.EmailUser;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: jenkin
 * @since: 2019/4/16 14:21
 * @history: 1.2019/4/16 created by jenkin
 */
public interface EmailSender {

    default JavaMailSenderImpl newMailSender(EmailUser emailUser){
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(emailUser.getHost());
         sender.setUsername(emailUser.getUserName());
        sender.setPassword(emailUser.getPassword());
        sender.setDefaultEncoding("Utf-8");
        Properties p = new Properties();
        p.setProperty("smtp.timeout",60000+"");
        p.setProperty("smtp.auth","true");
        sender.setJavaMailProperties(p);
        return sender;
    }

    EmailSender build(EmailUser emailUser);

    /**
     * 设置附件
     * @param fileObjects
     * @return
     */
    EmailSender setFileObject(String... fileObjects);

    /**
     * 构建发送器
     * @return
     */
    EmailSender buildEmailSender(JavaMailSender mailSender);
    /**
     * 邮件发送方
     * @param from
     * @return
     */
    EmailSender fromUser(String from);

    /**
     * 目标方
     * @param to
     * @return
     */
    EmailSender toUser(String... to);

    /**
     * 发送主题
     * @param subject
     * @return
     */
    EmailSender subject(String subject);

    /**
     * 发送内容
     * @param content
     * @return
     */
    EmailSender content(String content);

    /**
     * 抄送给谁
     * @param copyTo
     * @return
     */
    EmailSender copyTo(String... copyTo);

    /**
     * 密送给谁
     * @param securityTo
     * @return
     */
    EmailSender securityTo(String... securityTo);


    boolean send(EmailMsg msg);


}
