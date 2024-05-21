package com.csfrez.tool.socket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyServer {

    public static void main(String[] args) throws Exception {
        //设置接受网络连接线程池
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //设置处理网络除连接外所有事件线程的线程池
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)//设置Channel类型
                .option(ChannelOption.SO_BACKLOG, 1024)
                .handler(new ChannelInitializer<ServerSocketChannel>() {
                    @Override
                    protected void initChannel(ServerSocketChannel ch) throws Exception {
                        //设置处理网络连接的Handler
//                        ch.pipeline().addLast("serverBindHandler",
//                                new NettyBindHandler(NettyTcpServer.this, serverStreamLifecycleListeners));
                    }
                })
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
//                        ch.pipeline()
//                                .addLast("protocolHandler", new NettyProtocolHandler())//设置编解码器
//                                .addLast("serverIdleHandler", new IdleStateHandler(0, 0, serverIdleTimeInSeconds))//设置心跳检测
//                                .addLast("serverHandler", new NettyServerStreamHandler(NettyTcpServer.this, false,
//                                        serverStreamLifecycleListeners,
//                                        serverStreamMessageListeners));//设置业务处理逻辑
                    }
                });

        ChannelFuture channelFuture = serverBootstrap.bind(9000).sync();
        channelFuture.channel().closeFuture().sync();
    }
}