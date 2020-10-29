package com.jenkin.log.logplatform.heartbeat;

import com.jenkin.log.common.utils.NettyUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/7 11:48
 * @description：
 * @modified By：
 * @version: 1.0
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(HeartBeatClient.class);

    /**
     * 循环次数
     */
    private AtomicInteger fcount = new AtomicInteger(1);

    /**
     * 建立连接时
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        int port = ipSocket.getPort();
        String host = ipSocket.getHostString();
        logger.info("服务端建立连接====={}:{}" , host,port);
        ctx.fireChannelActive();
    }

    /**
     * 关闭连接时
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        int port = ipSocket.getPort();
        String host = ipSocket.getHostString();
        logger.info("服务端断开连接====={}:{}" , host,port);

        super.channelInactive(ctx);

    }

    /**
     * 业务逻辑处理
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        String message = NettyUtils.getMessage(byteBuf);
//        logger.info("Client接受的服务端信息 {}",message);

    }


}
