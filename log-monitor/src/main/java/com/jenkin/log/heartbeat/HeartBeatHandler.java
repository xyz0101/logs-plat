package com.jenkin.log.heartbeat;

import com.jenkin.log.common.constant.CommonConst;
import com.jenkin.log.common.utils.NettyUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

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
        logger.info("heart beat ok [{}]" , recieved);

        try {
            ctx.writeAndFlush(NettyUtils.getSendByteBuf(CommonConst.HEARTBEAT_SUCCESS_MSG));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }





}
