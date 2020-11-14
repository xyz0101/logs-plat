package com.fdd.contract.contractintelligentcommon.config;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.status.ErrorStatus;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fdd.contract.contractintelligentcommon.utils.ApplicationContextProvider;
import com.fdd.contract.contractintelligentcommon.utils.CommonUtils;
import com.fdd.contract.contractintelligentcommon.utils.KafkaUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.fdd.contract.contractintelligentcommon.constant.ThreadConst.LOG_RECORD_EXECUTOR_SERVICE;


/**
 * @author  jenkin
 * @param <E>
 */
@ConfigurationProperties(prefix = "log-record")
@Component("kafkaAppender")
public class KafkaAppender<E> extends OutputStreamAppender<E> {

    @Value("${log-record.partition}")
    public Integer partition;
    private Productor productor = new Productor();
    public static  String IP="未知来源";
    public static  String HOST_NAME="未知来源";
    static {
        try {
            InetAddress addr = InetAddress.getLocalHost();
            IP = addr.getHostAddress();
            HOST_NAME=addr.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        started = true;
    }
    /**
     * This method differentiates RollingFileAppender from its super class.
     */
    @Override
    protected void subAppend(E event) {
        LOG_RECORD_EXECUTOR_SERVICE.submit(()->{
            try {
                boolean existBean = ApplicationContextProvider.existBean("kafkaAppender");
                if (existBean &&event  instanceof LoggingEvent) {
                    KafkaAppender kafkaAppender = ApplicationContextProvider.getBean("kafkaAppender", KafkaAppender.class);
                    LoggingEvent loggingEvent = (LoggingEvent) event;
                    partition=kafkaAppender.partition;
                    if (loggingEvent.getLoggerName().startsWith("com.fdd")&&partition!=null) {
                        LayoutWrappingEncoder<E> encoder = (LayoutWrappingEncoder<E>) this.getEncoder();
                        String msg = encoder.getLayout().doLayout(event);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("ipAddr",IP);
                        jsonObject.put("hostName",HOST_NAME);
                        jsonObject.put("message",msg);
                        productor.produce("log_plat_contract_topic" ,partition, JSON.toJSONString(jsonObject) );
                    }
                }
            }catch (Exception e){
                System.err.println(e.getMessage());
            }
        });


    }



    static class Productor {
        private final KafkaProducer<String, String> producer;

        public Productor(){
            producer = new KafkaProducer<>(KafkaUtils.getKafkaProductProperties());
        }

        public void produce (String topic, int partition,String data) {
            Future<RecordMetadata> send = producer.send(new ProducerRecord<>(topic, partition, null, data));
        }



    }

    public static void main(String[] args) {
        Productor productor = new Productor();
        productor.produce("log_plat_contract_topic",1,"hahahhaha");
    }
}