package com.jenkin.log.logplatform;

import com.alibaba.fastjson.JSON;
import com.jenkin.log.common.utils.KafkaUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

/**
 * @author jenkin
 * @className Test
 * @description TODO
 * @date 2020/10/29 17:02
 */
public class Test {
    private static Logger logger = LoggerFactory.getLogger(Test.class);
    public static void main(String[] args) {
        consumer("test",new TopicPartition("log_plat_contract_topic", 0));
    }
    /**
     * 消费
     * @param key
     * @param partitions
     */
    public static  void consumer(String key, TopicPartition partitions){
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(KafkaUtils.getKafkaConsumerProperties());
        kafkaConsumer.assign(Collections.singletonList(partitions));
        logger.info("注册主题和分区：{}   {}",partitions.topic(),partitions.partition());
        while (true) {
            if(Thread.currentThread().isInterrupted()){
                logger.warn("线程中断，退出,注销节点：{}",key);
                break;
            }
            //如果分区总数发生变化，需要重新修改分区
            ConsumerRecords<String, String> records = kafkaConsumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {
                String value = record.value();
//                Object message = JSON.parseObject(value).get("message");
                logger.info("线程 {} ---- offset {}----收到消息----------[{}]  来自主题：{}  分区：{}"
                        ,Thread.currentThread().getId(),record.offset(),value,record.topic(),record.partition());

                kafkaConsumer.commitAsync();
            }
        }
        kafkaConsumer.close();
        logger.warn("线程：{} 关闭",key);
    }

}
