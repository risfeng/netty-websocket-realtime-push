package com.adotcode.nettysocket.common.constants;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 常量类
 *
 * @author risfeng
 * @date 2020/9/10
 */
public class Constants {

    private Constants() {
    }

    /**
     * 服务端握手协议存储Map
     */
    public static final Map<String, WebSocketServerHandshaker> SERVER_HAND_SHAKER_MAP = new ConcurrentHashMap<>(10);

    /**
     * 用户通道绑定存储Map
     */
    public static final Map<String, ChannelHandlerContext> USER_ONLINE_CHANNEL_MAP = new ConcurrentHashMap<>(10);


}
