package com.jenkin.log.utils;


import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/4 8:53
 * @description：
 * @modified By：
 * @version: 1.0
 */
public class EsUtils {
    /**
     * 创建索引(默认分片数为5和副本数为1)
     * @param indexName
     * @throws IOException
     */
    public static boolean createIndex(String indexName, ElasticsearchTemplate elasticsearchTemplate) throws Exception {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.settings(Settings.builder()
                // 设置分片数为3， 副本为2
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2)
        );

        // 这里创建索引结构
        request.mapping("_doc",generateBuilder());
        ActionFuture<CreateIndexResponse> createIndexResponseActionFuture = elasticsearchTemplate.getClient().admin().indices().create(request);
        CreateIndexResponse createIndexResponse = createIndexResponseActionFuture.get();
        // 指示是否所有节点都已确认请求
        boolean acknowledged = createIndexResponse.isAcknowledged();
        // 指示是否在超时之前为索引中的每个分片启动了必需的分片副本数
        boolean shardsAcknowledged = createIndexResponse.isShardsAcknowledged();
        if (acknowledged || shardsAcknowledged) {
            return true;
        }
        return false;
    }

    private static XContentBuilder generateBuilder() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");

            {
                builder.startObject("timestamp");
                {
                    builder.field("type", "date");
                }
                builder.endObject();
            }
            {
                builder.startObject("content");
                {
                    builder.field("type", "text");
//                    builder.field("analyzer", "ik_smart");
                }
                builder.endObject();
            }

            builder.endObject();
        }
        builder.endObject();

        return builder;
    }
}
