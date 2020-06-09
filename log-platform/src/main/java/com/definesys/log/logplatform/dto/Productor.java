package com.definesys.log.logplatform.dto;

import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.UUID;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: lin.zhou
 * @date: 2020/6/9 21:12
 * @history: 1.2020/6/9 created by lin.zhou
 */
public class Productor {
    private final KafkaProducer<String, String> producer;

    public Productor(){
        Properties props = new  Properties() ;
        props.put("bootstrap.servers","172.16.161.51:9002, 172.16.161.51:9003, 172.16.161.51:9004");// XXX服务器
        props.put("acks","all");
        props.put("retries",0); //retries = MAX无限重试，直到你意识到出现了问题
        props.put("batch.size",16384); //producer将试图批处理消息记录以减少请求次数.默认的批量处理消息字节数
        props.put("linger.ms",1); //延迟Ims发送，这项设置将通过增加小的延迟来完成--即，不是立即发送一条记录， producer将会等待给定的延迟时间以允许其他消息记录发送，这些消息记录可以批量处理
        props.put("buffer.memory",33554432); //producer可以用来缓存数据的内存大小。
        props.put("key.serializer","org.apache.kafka.common.serialization.IntegerSerializer");
        props.put("value.serializer","org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<String,String>(props);
    }

    public void produce (String topic, int partition) {
        int messageNo = 1;
        final int COUNT = 5;
        while (messageNo < COUNT) {
            String key = String.valueOf(messageNo);
            String data = String.format("hello KafkaProducer message %s from" + topic + " " + UUID.randomUUID(), key);
            try {
                System.out.println("发送消息：" + data);
                producer.send(new ProducerRecord<String, String>(topic,partition,null,data));
            } catch (Exception e) {
                e.printStackTrace();
            }
            messageNo++;
        }
    }
    public void produce1 (String topic, int partition ,LogInfoDTO logInfoDTO) {
        String data = JSONObject.toJSONString(logInfoDTO);
        try {
            System.out.println("发送消息：" + data);
            producer.send(new ProducerRecord<String, String>(topic,partition,null,data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String [] args) {
        new Productor().produce("hrTpoic" ,0) ;
    }
}
