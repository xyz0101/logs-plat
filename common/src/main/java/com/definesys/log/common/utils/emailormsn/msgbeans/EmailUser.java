package com.definesys.log.common.utils.emailormsn.msgbeans;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: jenkin
 * @since: 2019/8/15 10:57
 * @history: 1.2019/8/15 created by jenkin
 */
public class EmailUser {
    private String userName;
    private String password;
    private String host;

    public EmailUser(String userName, String password, String host) {
        this.userName = userName;
        this.password = password;
        this.host = host;
    }

    public EmailUser() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
