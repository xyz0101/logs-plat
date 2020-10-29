package com.jenkin.log.logplatform.config;

import com.alibaba.fastjson.JSONObject;
import com.jenkin.log.common.utils.KafkaUtils;
import com.jenkin.log.logplatform.dto.LogInfoDTO;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.UUID;

/**
 * @Copyright: Shanghai jenkin Company.All rights reserved.
 * @Description:
 * @author: lin.zhou
 * @date: 2020/6/9 21:12
 * @history: 1.2020/6/9 created by lin.zhou
 */
public class Productor {
    private final KafkaProducer<String, String> producer;

    public Productor(){
        producer = new KafkaProducer<String,String>(KafkaUtils.getKafkaProductProperties());
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
    public void produce1 (String topic, int partition , LogInfoDTO logInfoDTO) {
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
