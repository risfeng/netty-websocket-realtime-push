package com.adotcode.nettysocket.handler.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 子通道初始化处理器
 *
 * @author risfeng
 * @date 2020/9/6
 */
@Component
@ChannelHandler.Sharable
public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

    /**
     * 自定义http请求处理器
     */
    @Resource
    private HttpRequestHandler httpRequestHandler;

    /**
     * netty 服务端处理器
     */
    @Resource
    private NettyServerHandler nettyServerHandler;

    /**
     * 初始化对象，设置初 始化参数
     */
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // HTTP编码解码器
        ch.pipeline().addLast("http-codec", new HttpServerCodec());
        // 把HTTP头、HTTP体拼成完整的HTTP请求
        ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        // 方便大文件传输
        ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
        // 自定义http请求处理器
        ch.pipeline().addLast("http-request-handler", httpRequestHandler);
        // 自定义服务端处理器
        ch.pipeline().addLast("web-socket-handler", nettyServerHandler);
    }
}
