package com.jenkin.log.common.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author ：jenkin
 * @date ：Created at 2020/6/7 12:03
 * @description：
 * @modified By：
 * @version: 1.0
 */
public class NettyUtils {
    /*
     * 从ByteBuf中获取信息 使用UTF-8编码返回
     */
    public static String getMessage(ByteBuf buf) {

        byte[] con = new byte[buf.readableBytes()];
        buf.readBytes(con);
        return new String(con, StandardCharsets.UTF_8);
    }

    public static ByteBuf getSendByteBuf(String message)
            throws UnsupportedEncodingException {

        byte[] req = message.getBytes(StandardCharsets.UTF_8);
        ByteBuf pingMessage = Unpooled.buffer();
        pingMessage.writeBytes(req);

        return pingMessage;
    }
}
