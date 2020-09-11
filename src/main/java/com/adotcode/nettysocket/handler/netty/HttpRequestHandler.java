package com.adotcode.nettysocket.handler.netty;

import com.adotcode.nettysocket.common.utils.UrlUtils;
import com.adotcode.nettysocket.component.account.AccountComponent;
import com.adotcode.nettysocket.component.netty.NettyChannelComponent;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 自定义http请求处理器
 *
 * @author risfeng
 * @date 2020/9/6
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class HttpRequestHandler extends SimpleChannelInboundHandler<Object> {

    /**
     * 授权验证key
     */
    private static final String AUTHORIZATION_KEY = "token";

    /**
     * socket连接管理组件
     */
    @Resource
    private NettyChannelComponent nettyChannelComponent;

    /**
     * 用户管理组件
     */
    @Resource
    private AccountComponent accountComponent;


    /**
     * 处理HTTP请求，其他WebSocket请求交给下一个处理器处理
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            ctx.fireChannelRead(((WebSocketFrame) msg).retain());
        }
    }

    /**
     * 处理Http请求，升级HTTP协议到Websocket协议的升级
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        if (!req.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        // 解析参数 其中 token 参数必传
        Map<String, String> parameters = UrlUtils.parseParameter(req.uri());
        if (ObjectUtils.isEmpty(parameters)) {
            ByteBuf tipByteBuf = Unpooled.copiedBuffer("token参数为必传参数.", CharsetUtil.UTF_8);
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED, tipByteBuf));
            return;
        }
        // 获取授权token
        String token = parameters.get(AUTHORIZATION_KEY);
        Integer loginUserId = accountComponent.getLoginUserId(token);
        if (StringUtils.isBlank(token) || loginUserId <= 0) {
            ByteBuf unAuthorizationByteBuf = Unpooled.copiedBuffer("token验证失败.", CharsetUtil.UTF_8);
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.UNAUTHORIZED, unAuthorizationByteBuf));
            return;
        }
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws:/" + ctx.channel() + "/ws", null, false);
        WebSocketServerHandshaker handShaker = wsFactory.newHandshaker(req);
        // 登记socket握手
        nettyChannelComponent.bindChannelSocketHandShaker(ctx.channel().id().asLongText(), handShaker);
        // 登记用户上线
        nettyChannelComponent.bindUserOnline(loginUserId.toString(), ctx);
        if (handShaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handShaker.handshake(ctx.channel(), req);
        }
    }

    /**
     * 异常处理，关闭channel
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("处理异常", cause);
        ctx.close();
    }

    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse res) {
        // 没有内容时把返回状态作为内容返回给客户端
        if (res.status().code() != HttpStatus.OK.value() && !res.content().hasArray()) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        // 如非Keep-Alive，关闭连接
        boolean keepAlive = HttpUtil.isKeepAlive(req);
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!keepAlive) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
}
