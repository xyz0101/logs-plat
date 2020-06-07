package com.definesys.log.common.utils.emailormsn.email.impl;


import com.definesys.log.common.enums.EmailTypeEnum;
import com.definesys.log.common.utils.emailormsn.email.EmailSender;
import com.definesys.log.common.utils.emailormsn.msgbeans.EmailMsg;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: jenkin
 * @since: 2019/4/16 14:21
 * @history: 1.2019/4/16 created by jenkin
 */
@Component
public class TextEmailSender extends AbstractEmailSender {



    /**
     * 设置附件
     *
     * @param fileObjects
     * @return
     */
    @Override
    public EmailSender setFileObject(String... fileObjects) {
        return this;
    }

    @Override
    public EmailSender buildEmailSender(JavaMailSender mailSender) {
        this.mailMessage = new SimpleMailMessage();
        this.mailType = EmailTypeEnum.MAIL_TYPE_TEXT;
        this.mailSender = mailSender;
        return this;
    }

    @Override
    public boolean send(EmailMsg emailMsg) {
        try {
            this.toUser(emailMsg.getTo())
                    .fromUser(emailMsg.getFrom())
                    .subject(emailMsg.getSubject())
                    .content(emailMsg.getText())
                    .copyTo(emailMsg.getCopyTo())
                    .securityTo(emailMsg.getSecurityTo())
                    .setFileObject(emailMsg.getFilePath());
             mailSender.send(this.mailMessage);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


}
