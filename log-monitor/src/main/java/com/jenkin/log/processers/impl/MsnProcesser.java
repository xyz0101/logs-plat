package com.jenkin.log.processers.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jenkin.log.common.anno.OrderAndName;
import com.jenkin.log.common.entity.UserConfig;
import com.jenkin.log.common.utils.PlaceholderUtils;
import com.jenkin.log.entity.WarpperParam;
import com.jenkin.log.processers.AbstractProcessChain;
import com.jenkin.log.processers.ProcessChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author ：jenkin
 * @date ：Created at 2020/5/27 10:27
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Component
@OrderAndName(name = "msn",order = 3,note = "短信发送服务")
public class MsnProcesser extends AbstractProcessChain {
    private Logger logger = LoggerFactory.getLogger(MsnProcesser.class);

    @Override
    public void processMessage(WarpperParam<ProcessChain> param) {
        //解析日志消息
        final String logMsg = new String(param.getMessageContent());
        final String name = "msn";
        //获取通用的预警消息，该消息会在第一个processer MessageResolveProcesser里面解析好，这里直接拿就可以了
        Object extraMsg = param.getExtraMsg();
        final UserConfig userConfig = param.getUserConfig();
        if(userConfig !=null&& userConfig.getMsnPhoneNumber()!=null) {
            //如果有单独配置
            if (userConfig.getProcessersKeyWords() != null) {
                JSONObject jo = JSON.parseObject(userConfig.getProcessersKeyWords());
                Object msn = jo.get(name);
                //如果单独配置的关键字不为空并且有配置了当前模块的通知方式
                if (msn != null&& ((LinkedHashMap)msn).get("keyWords")!=null) {
                    Object keyWords = ((LinkedHashMap) msn).get("keyWords");
                    List<String> keywords = JSON.parseArray(keyWords.toString(), String.class);
                    if(!CollectionUtils.isEmpty(keywords)&&keywords.contains(name)) {
                        //如果没有单独配置模板那么就获取公用的模板
                        final String noticeTemplate =((LinkedHashMap) msn).get("noticeTemplate")==null?
                                param.getUserConfig().getNoticeTemplate():
                                String.valueOf(((LinkedHashMap) msn).get("noticeTemplate"));
                        String msg = PlaceholderUtils.resolvePlaceholders(noticeTemplate,JSON.parseObject(logMsg));
                        logger.info("发送短信给手机号：{}，发送的内容：{}", userConfig.getMsnPhoneNumber(), msg);
                    }
                }
            //如果没有单独配置就使用通用配置
            }else {
                logger.info("发送短信给手机号：{}，发送的内容：{}", userConfig.getMsnPhoneNumber(), extraMsg);
            }
        }

    }

}
