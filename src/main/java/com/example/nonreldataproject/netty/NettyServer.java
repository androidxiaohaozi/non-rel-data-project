package com.example.nonreldataproject.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author wh
 * @Date 2023/6/20 10:58
 * @Describe
 */
public class NettyServer implements Runnable {

    /** 异常输出 */
    private static final Logger LOGGER = LoggerFactory.getLogger(NettyServer.class);

    /**
     * soket监听
     */
    public static void soketListener() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        int port = 6001;
        try {
            LOGGER.info("================NettyServer 端口为：" + port + "========================");
            ServerBootstrap bootstrap = new ServerBootstrap();
            //绑定线程池
            bootstrap.group(bossGroup, workerGroup)
                    // 指定使用的channel
                    .channel(NioServerSocketChannel.class)
                    // 设置线程队列维护的连接个数
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.TCP_NODELAY, true)
                    // reuse addr，避免端口冲突
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 65535, 65535))
                    // 设置连接状态行为, 保持连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 绑定客户端连接时候触发操作
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            LOGGER.info("================有个socket客户端链接到本服务器, IP为：" + ch.remoteAddress().getHostName() + ", Port为：" + ch.remoteAddress().getPort() + "========================");
                            ch.pipeline().addLast(new ServerHandler());// 客户端触发操作
                        }
                    });
            // 服务器异步创建绑定
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync(); // 关闭服务器通道
        } catch (Exception e) {
            LOGGER.error("================NettyServer 端口为：" + port + " 启动出现异常， 异常内容为：" + e.getMessage() + "========================");
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void run() {
        NettyServer.soketListener();
    }
}