package com.tjmfreeone.tjmrpc.client.netty;

import com.tjmfreeone.tjmrpc.client.RpcClientService;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import com.tjmfreeone.tjmrpc.client.connection.ConnStatus;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyClient {


    private   NioEventLoopGroup worker;
    private  Bootstrap bootstrap;

    private  ChannelFuture channelFuture;

    private final String host;
    private final int port;
    private final URI uri;

    private final long retryConnectDelay;

    public NettyClient(String host, int port, URI uri, long retryConnectDelay){
        this.host = host;
        this.port = port;
        this.uri = uri;
        this.retryConnectDelay = retryConnectDelay;
    }

    public void prepare4connect(){
        worker = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(worker);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5 * 1000);
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new HttpClientCodec());
                pipeline.addLast(new ChunkedWriteHandler());
                pipeline.addLast(new HttpObjectAggregator(65535));
                pipeline.addLast(new WebSocketClientProtocolHandler(WebSocketClientHandshakerFactory.newHandshaker(
                        uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()
                )));
                log.info(uri+"####");
                pipeline.addLast(new IdleStateHandler(1, 5,0, TimeUnit.SECONDS));
                pipeline.addLast(new NettyClientHandler());
            }
        });
    }

    public Channel getChannel(){
        if(channelFuture.channel()!=null && channelFuture.channel().isActive()){
            return channelFuture.channel();
        }
        return null;
    }

    public  void loopRunConnect(){
        try{
            worker.scheduleWithFixedDelay(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    if(!RpcClientService.get().getConnStatus().equals(ConnStatus.OFF_LINE)){
                        return;
                    }

                    log.info("开始连接:"+host+":"+port+" ...");
                    RpcClientService.get().setConnStatus(ConnStatus.CONNECTING);
                    try {
                        channelFuture = bootstrap.connect(host, port).addListener(new ChannelFutureListener() {
                            @Override
                            public void operationComplete(ChannelFuture future) throws Exception {
                                if(future.isSuccess()){
                                    log.info("连接成功！");
                                    RpcClientService.get().setConnStatus(ConnStatus.ON_LINE);

                                } else {
                                    log.warn("连接失败, "+retryConnectDelay+"ms后重试");
                                    RpcClientService.get().setConnStatus(ConnStatus.OFF_LINE);
                                }
                            }
                        });
                    } catch (Exception e) {
                        log.info("loop connect error:"+e.toString());
                        RpcClientService.get().setConnStatus(ConnStatus.OFF_LINE);
                    }
                }
            }, 0, retryConnectDelay, TimeUnit.MILLISECONDS);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stopLoopConnect(){
        try {
            worker.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
