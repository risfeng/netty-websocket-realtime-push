package com.adotcode.nettysocket.component.netty;

import com.adotcode.nettysocket.common.constants.Constants;
import com.adotcode.nettysocket.common.result.HttpResult;
import com.adotcode.nettysocket.common.result.SocketMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Map;

/**
 * Netty Channel连接管理组件
 *
 * @author risfeng
 * @date 2020/9/6
 */
@Slf4j
@Component
public class NettyChannelComponent {

    /**
     * 通道与Socket握手协议绑定
     *
     * @param channelId 连接通道Id
     * @param shaker    当前握手处理器
     */
    public void bindChannelSocketHandShaker(String channelId, WebSocketServerHandshaker shaker) {
        if (ObjectUtils.isEmpty(channelId) || ObjectUtils.isEmpty(shaker)) {
            return;
        }
        Constants.SERVER_HAND_SHAKER_MAP.put(channelId, shaker);
    }

    /**
     * 用户上线绑定
     *
     * @param userId 用户Id
     * @param ctx    当前连接通道
     */
    public void bindUserOnline(String userId, ChannelHandlerContext ctx) {
        if (ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(ctx)) {
            return;
        }
        Constants.USER_ONLINE_CHANNEL_MAP.put(userId, ctx);
    }

    /**
     * 获取握手协议
     *
     * @param channelId 连接通道Id
     * @return 握手实例
     */
    public WebSocketServerHandshaker getHandShaker(String channelId) {
        return Constants.SERVER_HAND_SHAKER_MAP.get(channelId);
    }

    /**
     * 获取用户Socket连接
     *
     * @param userId 用户Id
     * @return {@link ChannelHandlerContext} 在线连接
     */
    public ChannelHandlerContext getChannel(String userId) {
        return Constants.USER_ONLINE_CHANNEL_MAP.get(userId);
    }

    /**
     * 移除握手连接
     *
     * @param channelId 连接通道Id
     * @return 移除成功数
     */
    public Boolean removeHandShaker(String channelId) {
        return Constants.SERVER_HAND_SHAKER_MAP.remove(channelId) != null;
    }

    /**
     * 私聊
     *
     * @param socketMessage socket消息
     * @param ctx           通道上下文
     */
    public void privateChat(SocketMessage<String> socketMessage, ChannelHandlerContext ctx) {
        ChannelHandlerContext toUserChannel = getChannel(socketMessage.getToUserId());
        if (ObjectUtils.isEmpty(toUserChannel)) {
            String failMessage = HttpResult.fail(MessageFormat.format("用户:[{0}]未在线。", socketMessage.getToUserId())).toString();
            sendMessage(ctx, failMessage);
            return;
        }
        String sendMessage = HttpResult.ok(socketMessage).toString();
        sendMessage(toUserChannel, sendMessage);
    }


    /**
     * 连接下线处理
     *
     * @param ctx 连接上下文
     */
    public void offline(ChannelHandlerContext ctx) {
        Iterator<Map.Entry<String, ChannelHandlerContext>> userOnlineChannelIterator = Constants.USER_ONLINE_CHANNEL_MAP.entrySet().iterator();
        while (userOnlineChannelIterator.hasNext()) {
            Map.Entry<String, ChannelHandlerContext> nextEntry = userOnlineChannelIterator.next();
            if (nextEntry.getValue().channel().id().asLongText().equals(ctx.channel().id().asLongText())) {
                removeHandShaker(ctx.channel().id().asLongText());
                log.info("成功移除握手实例.");
                userOnlineChannelIterator.remove();
                log.info(MessageFormat.format("用户:[{0}]已下线，当前在线人数为：{1}", nextEntry.getKey(), Constants.USER_ONLINE_CHANNEL_MAP.size()));
                break;
            }
        }
    }

    /**
     * 发送消息
     *
     * @param ctx     通道上下文
     * @param message 发送到消息
     */
    private void sendMessage(ChannelHandlerContext ctx, String message) {
        ctx.channel().writeAndFlush(new TextWebSocketFrame(message));
    }

}
