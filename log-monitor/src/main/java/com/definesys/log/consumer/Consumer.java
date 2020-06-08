package com.definesys.log.consumer;

import com.alibaba.fastjson.JSON;
import com.definesys.log.common.anno.LogConsumer;
import com.definesys.log.common.entity.UserConfig;
import com.definesys.log.common.utils.DBUtils;
import com.definesys.log.entity.ProcesserNode;
import com.definesys.log.entity.WarpperParam;
import com.definesys.log.processers.ProcessChain;
import com.definesys.log.utils.ProcesserUtil;
import com.definesys.log.common.utils.zk.ZkUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @author ：jenkin
 * @date ：Created at 2020/5/27 16:57
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Component
@LogConsumer
public class Consumer {
    /**
     * kafka的地址
     */
    private static final String IP_ADDR = "172.16.161.51:9002,172.16.161.51:9003,172.16.161.51:9004";
    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);
    /**
     * 线程容器，保存开启了的线程
     */
    private static final ConcurrentHashMap<String,Thread> THREAD_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();
    /**
     * 缓存各个系统的配置数据
     */
    private static final ConcurrentHashMap<String, UserConfig> USER_CONFIG_CONCURRENT_HASH_MAP = new ConcurrentHashMap<>();
    /**
     * 最大的线程数目
     */
    private static final int MAX_SIZE = 200;
    /**
     * 查询系统的配置数据的SQL
     */
    private static final String USER_CONFIG_SQL = "select * from user_system_config where config_id=? and object_version_number=?";
    /**
     * 线程池
     */
    private static final ExecutorService EXECUTOR_SERVICE =
            new ThreadPoolExecutor(10,MAX_SIZE,60,
                    TimeUnit.SECONDS,new ArrayBlockingQueue<>(1),new ThreadPoolExecutor.AbortPolicy());
    /**
     * 缓存所有的主题
     */
    private static final ConcurrentHashMap<String,TopicPartition> TOPICS = new ConcurrentHashMap<>();
    /**
     *当前服务的根路径，也就是模块名
     */
    private static final String ROOTPATH ;

    static {
        //启动服务
        ROOTPATH = startServer();
    }


    private static String  startServer() {
        //初始化客户端
        if (ZkUtils.client!=null) {
            ZkUtils.client.start();
        }
        //获取根路径
        String rootPath = ZkUtils.getModuleName();
        //启动服务，初始注册
        ZkUtils.createNode(ROOTPATH,"");
        List<String> subNodeValue = ZkUtils.getSubNodeValue();
        for (String node : subNodeValue) {
            if (node.contains("^")) {
                String[] split = node.split("\\^");
                String topic = split[0];
                String partitionStr = ZkUtils.resolvePartition(split[1], topic);
                TOPICS.put(node,new TopicPartition(topic,Integer.parseInt(partitionStr)));
            }
        }
        TOPICS.forEach(Consumer::submitThread);
        return rootPath;
    }

    /**
     * 提交消费者线程
     * @param key
     * @param val
     */
    private static void submitThread(String key, TopicPartition val) {
        if(THREAD_CONCURRENT_HASH_MAP.size()>=MAX_SIZE){
            throw new RuntimeException("线程资源已经达到最大数目："+MAX_SIZE);
        }
        String path = ROOTPATH+"/"+key;
        EXECUTOR_SERVICE.submit(()->{consumer(key,val);});
        String nodeData = ZkUtils.getNodeData(path);
        if (nodeData.contains(";")) {
            setConfigCache(path,nodeData);
        }
    }
    public static void main(String[] args){
        Consumer consumer = new Consumer();
        consumer.startConsumer();
    }

    /**
     * 消费者启动
     */
    public  void startConsumer() {
        Thread thread = new Thread(() -> {
            ZkUtils.zkWatch(ROOTPATH, this::onAdd, this::onUpdate);
        });
        thread.setName("kafka节点新增，删除监控线程");
        thread.start();

        //节点修改事件
        Thread updateThread = new Thread(() -> {
            ZkUtils.zkWatchUpdate(ROOTPATH, Consumer::setConfigCache);
        });
        updateThread.setName("kafka节点修改监控线程");
        updateThread.start();
    }

    /**
     *   节点删除事件
     * @param data
     */
    private void onUpdate(String data) {
        String node = data.replace(ROOTPATH + "/", "");
        TOPICS.remove(node);
        //线程信息
        THREAD_CONCURRENT_HASH_MAP.get(node).interrupt();
        //用户的配置信息
        USER_CONFIG_CONCURRENT_HASH_MAP.remove(node);
    }

    /**
     *   //节点添加事件
     * @param data
     */
    private void onAdd(String data) {
        String node = data.replace(ROOTPATH + "/", "");
        String[] split = node.split("\\^");
        String topic = split[0];
        String partitionStr = ZkUtils.resolvePartition(split[1], topic);
        TopicPartition partition = new TopicPartition(topic, Integer.parseInt(partitionStr));
        TopicPartition topicPartition = TOPICS.putIfAbsent(node, partition);
        if (topicPartition == null) {
            submitThread(node, partition);
        }
    }

    /**
     * 获取系统对应的配置
     * @param key
     * @param data
     */
    private static void setConfigCache(String key, String data) {
        key = key.replace(ROOTPATH + "/", "");

        if (data!=null) {
            data=data.replaceAll("\"","");
        }
        String[] split = data.split(";");
        int id = Integer.parseInt(split[0]);
        int version = Integer.parseInt(split[1]);
        Connection connection = DBUtils.getDBConnect();
        try {
            UserConfig userConfig = DBUtils.quickExeSelectResultForObject(connection, USER_CONFIG_SQL, UserConfig.class, id, version);
            if(userConfig!=null){
                USER_CONFIG_CONCURRENT_HASH_MAP.put(key,userConfig);
                logger.info("节点：{} 修改后的配置：{}",key,USER_CONFIG_CONCURRENT_HASH_MAP.get(key));
            }else{
                logger.warn("配置为空");
            }
        }finally {
            DBUtils.closeConn(connection);
        }

    }

    /**
     * 消费
     * @param key
     * @param partitions
     */
    public static  void consumer(String key, TopicPartition partitions){
        THREAD_CONCURRENT_HASH_MAP.putIfAbsent(key,Thread.currentThread());
        KafkaConsumer<String, String>  kafkaConsumer = new KafkaConsumer<>(getKafkaProperties());
        kafkaConsumer.assign(Collections.singletonList(partitions));
        logger.info("注册主题和分区：{}   {}",partitions.topic(),partitions.partition());
        while (true) {
            if(Thread.currentThread().isInterrupted()){
                logger.warn("线程中断，退出,注销节点：{}",key);
                break;
            }
            ConsumerRecords<String, String> records = kafkaConsumer.poll(1000);
            for (ConsumerRecord<String, String> record : records) {
                Object message = JSON.parseObject(record.value()).get("message");
                logger.info("线程 {} ---- offset {}----收到消息----------[{}]  来自主题：{}  分区：{}"
                ,Thread.currentThread().getId(),record.offset(),message,record.topic(),record.partition());
                dealMessage(key,record,kafkaConsumer);
                kafkaConsumer.commitAsync();
            }
        }
        kafkaConsumer.close();
        logger.warn("线程：{} 关闭",key);
    }

    /**
     * 属性配置
     * @return
     */
    private static Properties getKafkaProperties() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers",IP_ADDR);//xxx服务器ip
        properties.put("group.id", ZkUtils.getLocalIp()+UUID.randomUUID().toString());
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
     * 消息处理
     * @param key
     * @param record
     * @param kafkaConsumer
     */
    private static void dealMessage(String key,ConsumerRecord<String, String> record, KafkaConsumer<String, String> kafkaConsumer) {
        UserConfig userConfig = USER_CONFIG_CONCURRENT_HASH_MAP.get( key);
        String[] chains = new String[0];
        if (userConfig!=null&&userConfig.getIgnoreChains()!=null) {
            chains=userConfig.getIgnoreChains().split(";");
        }
        ProcesserNode<ProcessChain> processerNode =  ProcesserUtil.getProcesser(chains);
        WarpperParam<ProcessChain> warpperParam = new WarpperParam<>();
        warpperParam.setUserConfig(userConfig);
        warpperParam.setMessageContent(record.value().getBytes());
        warpperParam.setProcesserNode(processerNode);
        processerNode.value.process(warpperParam);
    }

}
