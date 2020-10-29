package com.jenkin.log.common.utils;

import com.google.common.collect.Lists;
import com.jenkin.log.common.utils.zk.ZkUtils;

import java.util.Properties;
import java.util.UUID;

/**
 * @author jenkin
 * @className KafkaUtils
 * @description TODO
 * @date 2020/10/28 16:51
 */
public class KafkaUtils {
    /**
     * kafka的地址
     */
    public static final String IP_ADDR = "47.102.210.165:7792";
    /**
     * 属性配置
     * @return
     */
    public static Properties getKafkaConsumerProperties() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers",IP_ADDR);//xxx服务器ip
        properties.put("group.id", ZkUtils.getLocalIp()+ UUID.randomUUID().toString());
//        properties.put("enable.auto.commit", "true");
//        properties.put("auto.commit.interval.ms", "1000");
//        properties.put("max.poll.records", 20);
        properties.put("auto.offset.reset", "latest");
//        properties.put("max.poll.interval.ms", "5000");
        properties.put("session.timeout.ms", "60000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return properties;
    }
    /**
     * 属性配置
     * @return
     */
    public static Properties getKafkaProductProperties() {
        Properties props = new  Properties() ;
        props.put("bootstrap.servers",IP_ADDR);// XXX服务器
        props.put("acks","all");
        props.put("retries",0); //retries = MAX无限重试，直到你意识到出现了问题
        props.put("batch.size",16384); //producer将试图批处理消息记录以减少请求次数.默认的批量处理消息字节数
        props.put("linger.ms",1); //延迟Ims发送，这项设置将通过增加小的延迟来完成--即，不是立即发送一条记录， producer将会等待给定的延迟时间以允许其他消息记录发送，这些消息记录可以批量处理
        props.put("buffer.memory",33554432); //producer可以用来缓存数据的内存大小。
        props.put("key.serializer","org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }


}
