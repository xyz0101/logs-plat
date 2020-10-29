package com.jenkin.log.common.utils.emailormsn.msgbeans;


import com.jenkin.log.common.utils.emailormsn.msgbeans.base.AbstractMsg;

/**
 * @Copyright: Shanghai jenkin Company.All rights reserved.
 * @Description:
 * @author: jenkin
 * @since: 2019/4/21 19:49
 * @history: 1.2019/4/21 created by jenkin
 */
public class MsnMsg extends AbstractMsg {
    @Override
    public MsnMsg setText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public MsnMsg setTo(String... to) {
        this.to = to;
        return this;
    }
}
