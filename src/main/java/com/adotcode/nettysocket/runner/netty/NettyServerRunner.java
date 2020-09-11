package com.adotcode.nettysocket.runner.netty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * Netty服务端应用控制入口
 *
 * @author risfeng
 * @date 2020/9/5
 */
@Slf4j
@Component
@Scope("singleton")
public class NettyServerRunner implements ApplicationRunner {

    /**
     * netty服务端启动类
     */
    @Resource
    private NettySocketServer nettySocketServer;

    /**
     * netty服务端管理线程
     */
    private final ThreadPoolTaskExecutor nettyRunThreadPoolExecutor;

    /**
     * 构造
     */
    public NettyServerRunner() {
        nettyRunThreadPoolExecutor = new ThreadPoolTaskExecutor();
        nettyRunThreadPoolExecutor.setCorePoolSize(1);
        nettyRunThreadPoolExecutor.setAllowCoreThreadTimeOut(true);
        nettyRunThreadPoolExecutor.setMaxPoolSize(1);
        nettyRunThreadPoolExecutor.setQueueCapacity(1);
        nettyRunThreadPoolExecutor.setThreadNamePrefix("netty-server-");
        nettyRunThreadPoolExecutor.initialize();
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info("Netty服务端线程池正在启动NettySocket服务器...");
        nettyRunThreadPoolExecutor.execute(nettySocketServer);
    }

    /**
     * 服务销毁时自动关闭、回收netty相关资源
     */
    @PreDestroy
    public void shutdown() {
        log.info("正在关闭与回收NettySocket相关资源...");
        nettySocketServer.shutdown();
        log.info("正在关闭与回收NettySocket服务器线程池资源...");
        nettyRunThreadPoolExecutor.shutdown();
        log.info("NettySocket关闭成功！");
    }
}
