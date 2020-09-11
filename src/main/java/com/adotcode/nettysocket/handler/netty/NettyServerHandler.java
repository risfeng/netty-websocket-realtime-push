package com.adotcode.nettysocket.handler.netty;

import com.adotcode.nettysocket.common.enums.SocketMessageTypeEnum;
import com.adotcode.nettysocket.common.result.HttpResult;
import com.adotcode.nettysocket.common.result.SocketMessage;
import com.adotcode.nettysocket.common.utils.GsonUtils;
import com.adotcode.nettysocket.component.netty.NettyChannelComponent;
import com.google.gson.reflect.TypeToken;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.MessageFormat;

/**
 * Netty服务端处理器
 *
 * @author risfeng
 * @date 2020/9/6
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    /**
     * Socket连接管理组件
     */
    @Resource
    private NettyChannelComponent nettyChannelComponent;

    /**
     * 读取消息处理
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) {
        handlerWebSocketFrame(ctx, msg);
    }

    /**
     * 处理WebSocketFrame
     */
    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 处理关闭请求
        if (frame instanceof CloseWebSocketFrame) {
            WebSocketServerHandshaker handShaker = nettyChannelComponent.getHandShaker(ctx.channel().id().asLongText());
            if (handShaker == null) {
                sendFailMessage(ctx, "连接不存在！");
            } else {
                handShaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            }
            return;
        }
        // ping请求
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 只支持文本格式，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            sendFailMessage(ctx, "仅支持文本消息。");
            return;
        }
        // 客服端发送过来的消息
        String message = ((TextWebSocketFrame) frame).text();
        if (StringUtils.isBlank(message)) {
            sendFailMessage(ctx, "消息内容为空。");
            return;
        }
        log.info("服务端接收到消息:[{}]", message);
        SocketMessage<String> socketMessage = GsonUtils.getInstance().fromJson(message, new TypeToken<SocketMessage<String>>() {
        }.getType());
        if (ObjectUtils.isEmpty(socketMessage)) {
            sendFailMessage(ctx, "参数解析为空。");
            return;
        }
        SocketMessageTypeEnum messageType = socketMessage.getMessageType();
        switch (messageType) {
            case PRIVATE_CHAT:
                nettyChannelComponent.privateChat(socketMessage, ctx);
                break;
            case SEND_TO_ALL:
            case GROUP_CHAT:
            case SYSTEM_NOTIFICATION:
            case REAL_TIME_ONLINE_STATISTICS:
            default:
                sendFailMessage(ctx, MessageFormat.format("未实现的消息类型。message:[{0}]", socketMessage));
                break;
        }
    }

    /**
     * 客户端断开连接
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        nettyChannelComponent.offline(ctx);
    }

    /**
     * 异常处理
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("socket exception", cause);
        ctx.close();
    }

    /**
     * 发送失败消息
     *
     * @param ctx         连接上下文
     * @param failMessage 失败消息
     */
    private void sendFailMessage(ChannelHandlerContext ctx, String failMessage) {
        String failResponseMessage = HttpResult.fail(failMessage).toString();
        ctx.channel().writeAndFlush(new TextWebSocketFrame(failResponseMessage));
    }
}
