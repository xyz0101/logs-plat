package com.jenkin.log.processers.impl;

import com.jenkin.log.common.anno.OrderAndName;
import com.jenkin.log.entity.WarpperParam;
import com.jenkin.log.processers.AbstractProcessChain;
import com.jenkin.log.processers.ProcessChain;
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
@OrderAndName(name = "wechat",order = 4,note = "微信通知服务")
public class WechatProcesser extends AbstractProcessChain {
    private Logger logger = LoggerFactory.getLogger(WechatProcesser.class);

    @Override
    public void processMessage(WarpperParam<ProcessChain> param) {
        logger.info("发送微信通知");
    }

}
