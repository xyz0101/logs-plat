package com.jenkin.log.hander;

import com.jenkin.log.common.anno.LogConsumer;
import com.jenkin.log.common.anno.OrderAndName;
import com.jenkin.log.common.utils.zk.ZkUtils;
import com.jenkin.log.consumer.Consumer;
import com.jenkin.log.heartbeat.HeartBeatHandler;
import com.jenkin.log.processers.AbstractProcessChain;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

/**
 * @author ：jenkin
 * @date ：Created at 2020/5/27 10:38
 * @description：
 * @modified By：
 * @version: 1.0
 */
@Component
public class AfterInitBeanPostProcessor  implements ApplicationListener<ContextRefreshedEvent>  {
   private static final Logger logger = LoggerFactory.getLogger(AfterInitBeanPostProcessor.class);

    /**
     * 容器启动完成之后会执行
     * @param contextRefreshedEvent
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        String[] beanNamesForType = contextRefreshedEvent.getApplicationContext().getBeanNamesForAnnotation(LogConsumer.class);
        System.out.println(Arrays.toString(beanNamesForType));
        for (String name : beanNamesForType) {
            Consumer bean = (Consumer) contextRefreshedEvent.getApplicationContext().getBean(name);
            //启动消费者
            bean.startConsumer();
        }
        //注册已经实现的处理节点
        registerProcessNode(contextRefreshedEvent.getApplicationContext());
        //获取随机可用的端口号
        int port =getPort();
        //注册系统节点名称
        registerSystemNodeName(ZkUtils.getLocalIp()+":"+port);
        //启动心跳检测服务
        startHeartbeatServer(port);

    }

    /**
     * 启动心跳检测
     * @param port
     */
    private void startHeartbeatServer(int port) {
        Thread thread = new Thread(()->{
            EventLoopGroup group = new NioEventLoopGroup();
            EventLoopGroup sub = new NioEventLoopGroup();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group,sub)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024) // 连接数
                    .option(ChannelOption.TCP_NODELAY, true) // 不延迟，消息立即发送
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 长连接
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel socketChannel) throws Exception {

                            ChannelPipeline p = socketChannel.pipeline();
                            p.addLast(new HeartBeatHandler());
                        }
                    });
            ChannelFuture channelFuture = null;
            try {
                channelFuture = serverBootstrap.bind(port).sync();
                if (channelFuture.isSuccess()) {
                    logger.info("启动心跳检测服务成功，端口号：" + port);
                }
                // 关闭连接
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                group.shutdownGracefully();
                sub.shutdownGracefully();
            }
        });
        thread.setDaemon(true);
        thread.setName("心跳检测服务端");
        thread.start();
    }

    /**
     * 获取可用端口
     * @return
     */
    private int getPort() {
        ServerSocket serverSocket = null; //读取空闲的可用端口
        try {
            serverSocket = new ServerSocket(0);
            int localPort = serverSocket.getLocalPort();
            serverSocket.close();
            return localPort;
        } catch (IOException e) {
            e.printStackTrace();
        }
       return -1;
    }

    private void registerSystemNodeName(String data) {
        ZkUtils.registerModuleName(data);
    }

    /**
     * 注册processer
     * @param applicationContext
     */
    private void registerProcessNode(ApplicationContext applicationContext) {
        if(ZkUtils.client.getState()!= CuratorFrameworkState.STARTED) {
            ZkUtils.client.start();
        }else{
            logger.warn("zkClient已经启动");
        }
        String[] beanNamesForType = applicationContext.getBeanNamesForType(AbstractProcessChain.class);
        if(beanNamesForType!=null){
            List<Map> nodes = new ArrayList<>();

            for (String bean : beanNamesForType) {
                Object obj = applicationContext.getBean(bean);
                if (obj.getClass().isAnnotationPresent(OrderAndName.class)) {
                    OrderAndName orderAndName =
                            obj.getClass().getDeclaredAnnotation(OrderAndName.class);
                    if(orderAndName.show()) {
                        Map<String, String> node = new HashMap<>();
                        node.put("name", orderAndName.name());
                        node.put("note", orderAndName.note());
                        nodes.add(node);
                    }
                }
            }
            ZkUtils.registerProcessNode(nodes);
        }
    }
}
