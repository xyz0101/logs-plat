package com.definesys.log.common.utils.emailormsn.email.impl;

import com.definesys.log.common.enums.EmailTypeEnum;
import com.definesys.log.common.utils.emailormsn.email.EmailSender;
import com.definesys.log.common.utils.emailormsn.msgbeans.EmailUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: jenkin
 * @since: 2019/4/16 14:21
 * @history: 1.2019/4/16 created by jenkin
 */
public abstract class AbstractEmailSender implements EmailSender {
    protected JavaMailSender mailSender;

    protected SimpleMailMessage mailMessage;

    protected MimeMessage mimeMessage;

    protected MimeMessageHelper helper;

    protected EmailTypeEnum mailType;
    @Value("${spring.mail.username}")
    protected String from;

    @Override
    public EmailSender build(EmailUser emailUser) {
        JavaMailSenderImpl javaMailSender = newMailSender(emailUser);

        return buildEmailSender(javaMailSender);
    }

    /**
     * 创建邮件发送器
     * @param emailTypeEnum
     * @return
     */
    public static EmailSender createEmailSender(EmailTypeEnum emailTypeEnum){

        switch (emailTypeEnum){
            case MAIL_TYPE_PIC:
                return new WithPicEmailSender();
            case MAIL_TYPE_FILE:
                return new WithFileEmailSender();
            case MAIL_TYPE_HTML:
                return new HtmlEmailSender();
            case MAIL_TYPE_TEXT:
                return new TextEmailSender();
            default:
                return null;
        }
    }


    /**
     * 发送主题
     *
     * @param subject
     * @return
     */
    @Override
    public EmailSender subject(String subject) {
        try {
            if(mimeMessage!=null){
                this.helper.setSubject(subject);
            }else if(mailMessage!=null){
                this.mailMessage.setSubject(subject);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return this;
    }
    /**
     * 邮件发送方
     *
     * @param from
     * @return
     */
    @Override
    public EmailSender fromUser(String from) {
        try {
            if(mimeMessage!=null){
                this.helper.setFrom(from);
            }else if(mailMessage!=null){
                this.mailMessage.setFrom(from);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 目标方
     *
     * @param to
     * @return
     */
    @Override
    public EmailSender toUser(String... to) {
        try {
            if(mimeMessage!=null){
                this.helper.setTo(to);
            }else if(mailMessage!=null){
                this.mailMessage.setTo(to);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 抄送给谁
     *
     * @param copyTo
     * @return
     */
    @Override
    public EmailSender copyTo(String... copyTo) {
        try {
            if(mimeMessage!=null){
                this.helper.setCc(copyTo);
            }else if(mailMessage!=null){
                this.mailMessage.setCc(copyTo);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return this;
    }

    /**
     * 密送给谁
     *
     * @param securityTo
     * @return
     */
    @Override
    public EmailSender securityTo(String... securityTo) {
        try {
            if(mimeMessage!=null){
                this.helper.setBcc(securityTo);
            }else if(mailMessage!=null){
                this.mailMessage.setBcc(securityTo);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return this;
    }
    /**
     * 发送内容
     *
     * @param content
     * @return
     */
    @Override
    public EmailSender content(String content) {
        try {
            if(mimeMessage!=null){
                this.helper.setText(content,true);
            }else if(mailMessage!=null){
                this.mailMessage.setText(content);
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return this;
    }
}
