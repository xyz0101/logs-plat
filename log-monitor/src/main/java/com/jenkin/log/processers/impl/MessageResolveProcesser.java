package com.jenkin.log.processers.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jenkin.log.common.anno.OrderAndName;
import com.jenkin.log.common.utils.PlaceholderUtils;
import com.jenkin.log.entity.WarpperParam;
import com.jenkin.log.processers.AbstractProcessChain;
import com.jenkin.log.processers.ProcessChain;
import com.jenkin.log.utils.ProcesserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/3 12:17
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Component
@OrderAndName(name = "resolve",order = 1,note = "消息处理",show = false)
public class MessageResolveProcesser extends AbstractProcessChain {
    private Logger logger = LoggerFactory.getLogger(MessageResolveProcesser.class);

    /**
     * 处理消息的核心逻辑，需要实现
     *
     * @param param
     */
    @Override
    public void processMessage(WarpperParam<ProcessChain> param) {
         String logMsg = new String(param.getMessageContent());
        if(param.getUserConfig()!=null&&param.getUserConfig().getKeyWords()!=null) {

            String msg=null;
            // 判读是否包含了关键字
            if (ProcesserUtil.checkHasKeywords(param.getUserConfig().getKeyWords(),logMsg)) {
                try {
                    JSONObject jsonObject = JSON.parseObject(logMsg);
                    //进行占位符替换
                    msg = PlaceholderUtils.resolvePlaceholders(param.getUserConfig().getNoticeTemplate(),jsonObject);
                }catch (Exception e){
                    msg =param.getUserConfig().getNoticeTemplate();
                    logger.error(e.getMessage(),e);
                }
                //进行日志内容拼接
                if(param.getUserConfig().getNeedLogContent()!=null&&param.getUserConfig().getNeedLogContent()==1){
                    msg=msg+"\n";
                    msg=msg+logMsg;
                }
                logger.info("模板解析完成");
                param.setExtraMsg(msg);
            }
        }



    }
}
