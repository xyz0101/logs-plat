package com.jenkin.log.common.utils.emailormsn.email.impl;


import com.jenkin.log.common.enums.EmailTypeEnum;
import com.jenkin.log.common.utils.emailormsn.email.EmailSender;
import com.jenkin.log.common.utils.emailormsn.msgbeans.EmailMsg;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.File;
import java.util.Arrays;

/**
 * @Copyright: Shanghai jenkin Company.All rights reserved.
 * @Description:
 * @author: jenkin
 * @since: 2019/4/16 17:10
 * @history: 1.2019/4/16 created by jenkin
 */
@Component
public class WithPicEmailSender extends AbstractEmailSender {

    @Override
    public EmailSender buildEmailSender(JavaMailSender mailSender) {
        this.mailType = EmailTypeEnum.MAIL_TYPE_PIC;
        this.mimeMessage = mailSender.createMimeMessage();
        this.mailSender = mailSender;
        try {
            this.helper = new MimeMessageHelper(this.mimeMessage, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }


        return this;
    }
    /**
     * 设置文件
     * @param pics
     * @return
     */
    @Override
    public EmailSender setFileObject(String... pics){
        Arrays.stream(pics).forEach(filePath->{
            FileSystemResource pic = new FileSystemResource(new File(filePath));
            try {
                String rscId = pic.getFilename();
                this.helper.addInline(rscId, pic);

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });
        return this;
    }
    @Override
    public boolean send(EmailMsg emailMsg) {
        try {
               this.toUser(emailMsg.getTo())
                    .subject(emailMsg.getSubject())
                    .content(emailMsg.getText())
                       .fromUser(emailMsg.getFrom())
                    .copyTo(emailMsg.getCopyTo())
                    .securityTo(emailMsg.getSecurityTo())
                    .setFileObject(emailMsg.getFilePath());
            mailSender.send(this.mimeMessage);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
