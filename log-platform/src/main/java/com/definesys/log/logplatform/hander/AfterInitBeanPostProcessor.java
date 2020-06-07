package com.definesys.log.logplatform.hander;

import com.definesys.log.common.utils.T;
import com.definesys.log.common.utils.zk.ZkUtils;
import com.definesys.log.logplatform.heartbeat.HeartBeatClient;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelFuture;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author ：jenkin
 * @date ：Created at 2020/5/27 10:38
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Component
public class AfterInitBeanPostProcessor implements ApplicationListener<ContextRefreshedEvent>  {
    private Logger logger= LoggerFactory.getLogger(AfterInitBeanPostProcessor.class);
    @Autowired
    HeartBeatClient heartBeatClient;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if(ZkUtils.client.getState()!= CuratorFrameworkState.STARTED) {
            ZkUtils.client.start();
        }else{
            logger.warn("zkClient已经启动");
        }

        //获取已注册的服务
        List<String> registeredModuleNames = ZkUtils.getRegisteredModuleNamesNoPath();
        Map<String,List<Object>> hosts = getHostOrPort(registeredModuleNames);
        //发送心跳检测数据包
        startHeartCheck();
        //启动心跳检测客户端
        startHeartBeatClient(hosts);

        logger.info("启动完成");


    }

    private void startHeartCheck() {

        Thread thread  = new Thread(() -> {
            while(true ) {
                List<String> names = ZkUtils.getRegisteredModuleNamesNoPath();

                logger.info("所有的服务：{}",names);
                names.forEach(item->{
                    String nodeData = ZkUtils.getNodeData(ZkUtils.MODULE_NAME_PATH+"/"+item);
                    String[] split = nodeData.split(":");
                    logger.info("服务端数目：{}",HeartBeatClient.channels.size());
                    ChannelFuture channelFuture = HeartBeatClient.channels.get(item);
                    if (channelFuture == null || !channelFuture.channel().isActive()) {
                        startHeartBeatClient(split[0],Integer.parseInt(split[1]),item);
                    }
                    try {
                        heartBeatClient.sendMsg(channelFuture,"are you ok?");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        });
        thread.setName("心跳检测");
        thread.start();
    }

    private void startHeartBeatClient(Map<String, List<Object>> hosts) {
        Thread thread = new Thread(()->{
            HeartBeatClient.startClient(hosts.get("host"),hosts.get("port"), (Map) hosts.get("names").get(0));
        });
        thread.setName("心跳检测客户端");
        thread.start();
    }
    private void startHeartBeatClient( String host,int port,String nodeName) {
        Thread thread = new Thread(()->{
            Map<String,String> nodeNames = new HashMap<>();
            nodeNames.put(host+":"+port,nodeName);
            HeartBeatClient.startClient(Collections.singletonList(host),Collections.singletonList(port),nodeNames );
        });
        thread.setName("心跳检测客户端");
        thread.start();
    }
    private Map<String, List<Object>> getHostOrPort(List<String> registeredModuleNames) {
        Map<String, List<Object>> res = new HashMap<>();
        List<Object> hosts = new ArrayList<>();
        List<Object> ports = new ArrayList<>();
        List<Object> nodeNames = new ArrayList<>();
        Map<String,String> nodeName = new HashMap<>();
        if(!CollectionUtils.isEmpty(registeredModuleNames)){
            registeredModuleNames.forEach(item->{
                String nodeData = ZkUtils.getNodeData(ZkUtils.MODULE_NAME_PATH+"/"+item);
                logger.info("服务：{}",nodeData);
                nodeName.put(nodeData,item);
                String[] split = nodeData.split(":");
                hosts.add(split[0]);
                ports.add(Integer.parseInt(split[1]));
            });
        }
        res.put("host",hosts);
        res.put("port",ports);
        nodeNames.add(nodeName);
        res.put("names",nodeNames);
        return res;
    }
}
