package com.tiutiu.consumer;

import com.tiutiu.common.RpcRequest;
import com.tiutiu.common.ServiceMeta;
import com.tiutiu.consumer.handler.RpcClientHandler;
import com.tiutiu.protocol.RpcProtocol;
import com.tiutiu.protocol.codec.RpcDecoder;
import com.tiutiu.protocol.codec.RpcEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;

import java.util.logging.Logger;

public class RpcClient{
    Logger logger = Logger.getLogger(RpcClient.class.getName());
    private final String hostName = "localhost";
    private final Integer port = 8088;
    private Bootstrap bootstrap;
    private EventLoopGroup eventLoopGroup;
    public RpcClient(){
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new RpcEncoder())
                                .addLast(new RpcDecoder())
                                .addLast(new RpcClientHandler());
                    }
                });
    }
    public void sendRequest(ServiceMeta serviceMeta, RpcProtocol<RpcRequest> protocol) throws Exception{
        ChannelFuture future = bootstrap.connect(serviceMeta.getServiceAddress(), serviceMeta.getServicePort()).sync();
        future.addListener((ChannelFutureListener) channelFuture -> {
            if (future.isSuccess()) {
                logger.info("客户端连接成功");
            } else {
                logger.info("客户端连接失败");
            }
        });
        future.channel().writeAndFlush(protocol);
    }

}
