package com.adotcode.nettysocket.runner.netty;

import com.adotcode.nettysocket.handler.netty.ChildChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Netty Web Socket 服务端
 *
 * @author risfeng
 * @date 2020/9/5
 */
@Slf4j
@Component
public class NettySocketServer implements Runnable {

    /**
     * netty 服务端端口号，默认：9090
     */
    @Value("${server.netty.port:9090}")
    private int port;

    /**
     * netty处理TCP连接事件线程组（主）
     */
    private EventLoopGroup bossGroup;

    /**
     * netty处理IO事件线程组（从）
     */
    private EventLoopGroup workerGroup;

    /**
     * 绑定I/O事件的处理器
     */
    @Resource
    private ChildChannelHandler childChannelHandler;

    /**
     * I/O事件操作异步结果
     */
    private ChannelFuture serverChannelFuture;


    /**
     * 启动服务
     */
    @Override
    public void run() {
        builder();
    }

    /**
     * 构造Netty启动参数配置
     */
    public void builder() {
        // 一主多从模式
        bossGroup = new NioEventLoopGroup(1);
        // 默认线程数：内核数*2
        workerGroup = new NioEventLoopGroup();
        // 服务端启动引导
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            long begin = System.currentTimeMillis();
            serverBootstrap.group(bossGroup, workerGroup)
                    // 使用NioServerSocketChannel作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)
                    // 配置TCP参数，握手字符串长度设置
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    // 开启心跳包活机制，就是客户端、服务端建立连接处于ESTABLISHED状态，超过2小时没有交流，机制会被启动
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 配置固定长度接收缓存区分配器
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(592048))
                    // 绑定I/O事件的处理类
                    .childHandler(childChannelHandler);
            serverChannelFuture = serverBootstrap.bind(port).sync();
            long end = System.currentTimeMillis();
            log.info("NettyWebSocket服务器启动完成，耗时:{}ms，绑定端口:{}", end - begin, port);
        } catch (Exception e) {
            log.error("NettyWebSocket服务器启动异常。", e);
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 关闭、回收资源
     */
    public void shutdown() {
        serverChannelFuture.channel().close();
        Future<?> bossGroupFuture = bossGroup.shutdownGracefully();
        Future<?> workerGroupFuture = workerGroup.shutdownGracefully();
        try {
            bossGroupFuture.await();
            workerGroupFuture.await();
        } catch (Exception ex) {
            log.error("关闭NettyServer资源异常。", ex);
        }
    }
}
