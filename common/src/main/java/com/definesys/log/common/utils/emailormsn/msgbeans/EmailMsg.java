package com.definesys.log.common.utils.emailormsn.msgbeans;



import com.definesys.log.common.constant.CommonConst;
import com.definesys.log.common.enums.EmailTypeEnum;
import com.definesys.log.common.utils.emailormsn.msgbeans.base.AbstractMsg;

import java.util.Arrays;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: jenkin
 * @since: 2019/4/21 19:43
 * @history: 1.2019/4/21 created by jenkin
 */

public class EmailMsg extends AbstractMsg {
        @Deprecated
        private String from;
        private String subject;
        private String[] copyTo;
        private String[] securityTo;
        private String[] filePath;
        private EmailTypeEnum emailType;
        private EmailUser emailUser;
        @Override
        public EmailMsg setText(String text) {
            this.text = text;
            return this;
        }

        @Override
        public EmailMsg setTo(String... to) {
            this.to = to;
            return this;
        }

        public String getSubject() {
            return subject;
        }

        public EmailMsg setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public String[] getCopyTo() {
            return copyTo==null?new String[]{}:copyTo ;
        }

        public EmailMsg setCopyTo(String... copyTo) {
            this.copyTo = copyTo;
            return this;
        }

        public String[] getSecurityTo() {
            return securityTo==null?new String[]{}:securityTo;
        }

        public EmailMsg setSecurityTo(String... securityTo) {
            this.securityTo = securityTo;
            return this;
        }

        public String[] getFilePath() {
            return filePath;
        }

        public EmailMsg setFilePath(String... filePath) {
            this.filePath = filePath;
            return this;
        }


        public EmailTypeEnum getEmailType() {
            return emailType;
        }

        public EmailMsg setEmailType(EmailTypeEnum emailType) {
            this.emailType = emailType;
            return this;
        }

    @Override
    public String toString() {
        return "EmailMsg{" +
                "subject='" + subject + '\'' +
                ", copyTo=" + Arrays.toString(copyTo) +
                ", securityTo=" + Arrays.toString(securityTo) +
                ", filePath=" + Arrays.toString(filePath) +
                ", emailType=" + emailType +
                ", text='" + text + '\'' +
                ", to=" + Arrays.toString(to) +
                '}';
    }

    public String getFrom() {
        return  emailUser==null? CommonConst.DEFAULT_EMAIL_USER.getUserName():emailUser.getUserName();
    }
    /**
     * 此方法不会生效，如果需要配置发送人请调用 setEmailUser
     */
    @Deprecated
    public EmailMsg setFrom(String from) {
        this.from = from;
        return this;
    }

    public EmailUser getEmailUser() {
        return emailUser==null? CommonConst.DEFAULT_EMAIL_USER:emailUser;
    }

    /**
     * 设置邮件登录用户
     * @param emailUser
     * @return
     */
    public EmailMsg setEmailUser(EmailUser emailUser) {
        this.emailUser = emailUser;
        return this;
    }
}
