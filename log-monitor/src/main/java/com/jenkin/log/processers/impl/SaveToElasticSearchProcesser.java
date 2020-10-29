package com.jenkin.log.processers.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jenkin.log.common.anno.OrderAndName;
import com.jenkin.log.common.entity.UserConfig;
import com.jenkin.log.entity.StandardMsg;
import com.jenkin.log.entity.WarpperParam;
import com.jenkin.log.processers.AbstractProcessChain;
import com.jenkin.log.processers.ProcessChain;
import com.jenkin.log.utils.IndexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/4 9:21
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Component
@OrderAndName(name = "savees",note = "保存到es",order = 5,show = false)
public class SaveToElasticSearchProcesser extends AbstractProcessChain {
    private Logger logger = LoggerFactory.getLogger(SaveToElasticSearchProcesser.class);
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 处理消息的核心逻辑，需要实现
     *
     * @param param
     */
    @Override
    public void processMessage(WarpperParam<ProcessChain> param) {
        String data = new String(param.getMessageContent());
        UserConfig userConfig = param.getUserConfig();
        String indexName = IndexUtil.getNewIndexName(userConfig.getIndexSuffix(),userConfig.getPlaceOnFileTime(),
                userConfig.getPlaceOnFileTimeUnit(),userConfig.getSystemCode());
        indexName=indexName.toLowerCase();
        logger.info("索引名称：{}",indexName);

        if(!elasticsearchTemplate.indexExists(indexName)){
            logger.info("索引不存在，创建索引");
            elasticsearchTemplate.createIndex(indexName);

        }
        try {
            JSONObject jsonObject = JSON.parseObject(data);
            Object message = jsonObject.get("message");
            JSONObject jsonMessage = JSON.parseObject(String.valueOf(message));
            jsonObject.put("message",jsonMessage);
            data = JSON.toJSONString(jsonObject);
        } catch (Exception e) {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content",data);
            jsonObject.put("@timestamp",new Date());
            data = JSON.toJSONString(jsonObject);
            logger.error(e.getMessage());
        }
        IndexQuery index = new IndexQueryBuilder().withIndexName(indexName).withType("_doc").withSource(data).build();
        elasticsearchTemplate.bulkIndex(Collections.singletonList(index));
        logger.info("保存到ES");

    }
}
