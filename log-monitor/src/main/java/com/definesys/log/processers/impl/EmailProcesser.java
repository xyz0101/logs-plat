package com.definesys.log.processers.impl;

import com.definesys.log.common.anno.OrderAndName;
import com.definesys.log.common.constant.CommonConst;
import com.definesys.log.common.enums.EmailTypeEnum;
import com.definesys.log.entity.WarpperParam;
import com.definesys.log.processers.AbstractProcessChain;
import com.definesys.log.processers.ProcessChain;
import com.definesys.log.common.utils.emailormsn.email.impl.AbstractEmailSender;
import com.definesys.log.common.utils.emailormsn.msgbeans.EmailMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author ：jenkin
 * @date ：Created at 2020/5/27 10:27
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Component
@OrderAndName(name = "email",order = 2,note = "邮件发送服务")
public class EmailProcesser extends AbstractProcessChain {
    private Logger logger = LoggerFactory.getLogger(EmailProcesser.class);

    @Override
    public void processMessage(WarpperParam<ProcessChain> param) {
        Object extraMsg = param.getExtraMsg();
        EmailMsg emailMsg= null;
        if(param.getUserConfig()!=null&&param.getUserConfig().getKeyWords()!=null&& param.getUserConfig().getTargetEmails()!=null) {

            String msg=null;
            if (extraMsg!=null) {
                msg=String.valueOf(extraMsg);
                emailMsg = new EmailMsg()
                        .setEmailType(EmailTypeEnum.MAIL_TYPE_HTML)
                        .setEmailUser(CommonConst.DEFAULT_EMAIL_USER)
                        .setTo(param.getUserConfig().getTargetEmails().split(";"))
                        .setText(msg);
                AbstractEmailSender.createEmailSender(EmailTypeEnum.MAIL_TYPE_TEXT)
                        .build(CommonConst.DEFAULT_EMAIL_USER)
                        .send(emailMsg);
                logger.info("发送邮件");

            }
        }

    }

}
