package com.definesys.log.common.utils.emailormsn.msgbeans.base;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: jenkin
 * @since: 2019/4/21 19:47
 * @history: 1.2019/4/21 created by jenkin
 */
public abstract class AbstractMsg {
    protected String text;
    protected String[] to;

    public String getText() {
        return text;
    }

    public abstract AbstractMsg setText(String text) ;

    public String[] getTo() {
        return to;
    }

    public abstract AbstractMsg setTo(String... to);
}
