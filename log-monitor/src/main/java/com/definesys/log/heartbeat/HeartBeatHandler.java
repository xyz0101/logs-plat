package com.definesys.log.heartbeat;

import com.definesys.log.common.constant.CommonConst;
import com.definesys.log.common.utils.NettyUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/7 11:21
 * @description：
 * @modified By：
 * @version: 1.0
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(ChannelInboundHandlerAdapter.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ByteBuf buf = (ByteBuf) msg;

        String recieved = NettyUtils.getMessage(buf);
        logger.info("服务器接收到客户端消息：" + recieved);

        try {
            ctx.writeAndFlush(NettyUtils.getSendByteBuf(CommonConst.HEARTBEAT_SUCCESS_MSG));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }





}
