package com.definesys.log.logplatform.heartbeat;

import com.definesys.log.common.utils.NettyUtils;
import com.definesys.log.common.utils.zk.ZkUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/7 11:26
 * @description： 心跳检测客户端
 * @modified By：
 * @version: 1.0
 */
@Component
public class HeartBeatClient {
    private static Logger logger = LoggerFactory.getLogger(HeartBeatClient.class);
    /**
     * 当前活动的连接的信道，节点名称和信道的映射
     */
    public static ConcurrentHashMap<String, ChannelFuture> channels =new ConcurrentHashMap<>();
    private static final int MAX = 3;
    /**
     * 节点名称和连接状态的映射关系
     */
    public static final Map<String,Boolean> SERVER_STATUS = new HashMap<>();
    /**
     * 初始化Bootstrap
     */
    public static final Bootstrap getBootstrap(EventLoopGroup group) {
        if (null == group) {
            group = new NioEventLoopGroup();
        }
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.handler(new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new IdleStateHandler(0, 30, 0, TimeUnit.SECONDS));
                pipeline.addLast(new HeartBeatHandler());
            }
        });
        return bootstrap;
    }

    /**
     * 启动客户端
     * @param hosts
     * @param ports
     * @param nodeNames
     */
    public static  void startClient(List<Object> hosts, List<Object> ports,Map<String,String> nodeNames) {
        Bootstrap bootstrap = getBootstrap(null);
        for (int i = 0; i < hosts.size(); i++) {
            String host = (String) hosts.get(i);
            int port = (int) ports.get(i);
            String nodeName = nodeNames.get(host + ":" + port);
            bootstrap.remoteAddress(host, port);
            //异步连接tcp服务端
            ChannelFuture future = bootstrap.connect().addListener((ChannelFuture futureListener) -> {
                final EventLoop eventLoop = futureListener.channel().eventLoop();
                if (!futureListener.isSuccess()) {
                    //服务器未启动 连接tcp服务器不成功
                    logger.info( "第一次连接与服务端断开连接!在5s之后准备尝试重连!");
                    //5秒后重连
                    eventLoop.schedule(() -> doConnect(bootstrap, host, port,0,nodeName), 5, TimeUnit.SECONDS);
                }
            });
            channels.put(nodeName, future);
        }
    }

    /**
     * 重新连接tcp服务端
     */
    public static void doConnect(Bootstrap bootstrap, String host, int port,int count, String nodeName) {
        try {
            if (bootstrap != null) {
                bootstrap.remoteAddress(host, port);
                ChannelFuture f = bootstrap.connect().addListener((ChannelFuture futureListener) -> {
                    final EventLoop eventLoop = futureListener.channel().eventLoop();
                    if (!futureListener.isSuccess() ) {
                        if(count<MAX) {
                            ZkUtils.offlineModuleName(nodeName);
                            channels.remove(nodeName);
                            //连接tcp服务器不成功 5s后重连
                            logger.info(port + "服务器断线-----与服务端断开连接!在5s之后准备尝试重连!");
                            eventLoop.schedule(() -> doConnect(bootstrap, host, port, count + 1, nodeName), 5, TimeUnit.SECONDS);
                        }else{
                            SERVER_STATUS.put(nodeName,false);
                            channels.remove(nodeName);
                        }
                    }
                });
                if(f.isSuccess()) {
                    channels.put(nodeName, f);
                    SERVER_STATUS.put(nodeName, true);
                }
            }
        } catch (Exception e) {
            logger.error("客户端连接失败!" + e.getMessage());
        }

    }

    /**
     * 消息发送方法
     * @param future 信道
     * @param msg 消息
     * @throws Exception
     */
    public void sendMsg(ChannelFuture future, String msg) throws Exception {
        logger.info("开始发送消息");
        if (future != null && future.channel().isActive()) {
            Channel channel = future.channel();
            InetSocketAddress ipSocket = (InetSocketAddress) channel.remoteAddress();
            int port = ipSocket.getPort();
            String host = ipSocket.getHostString();
            logger.info("向服务端发消息：" +host+":" +port);
            channel.writeAndFlush(NettyUtils.getSendByteBuf(msg)).sync();
        } else {
            logger.error("消息发送失败,连接尚未建立!");
        }
    }





}
