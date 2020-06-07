package com.definesys.log.processers.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.definesys.log.common.anno.OrderAndName;
import com.definesys.log.common.utils.PlaceholderUtils;
import com.definesys.log.entity.WarpperParam;
import com.definesys.log.processers.AbstractProcessChain;
import com.definesys.log.processers.ProcessChain;
import com.definesys.log.utils.ProcesserUtil;
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
            if (ProcesserUtil.checkHasKeywords(param.getUserConfig().getKeyWords(),logMsg)) {
                try {
                    JSONObject jsonObject = JSON.parseObject(logMsg);
                    msg = PlaceholderUtils.resolvePlaceholders(param.getUserConfig().getNoticeTemplate(),jsonObject);
                }catch (Exception e){
                    logger.error(e.getMessage(),e);
                }
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
