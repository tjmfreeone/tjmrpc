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
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Client {

    public static NioEventLoopGroup worker;
    private static Bootstrap bootstrap;

    private static ChannelFuture channelFuture;

    private static String host = RpcClientService.get().getHost();
    private static int port = RpcClientService.get().getPort();

    @Setter
    @Getter
    public static long DELAY = 5000L;

    public static void spawn(URI uri){
        try{
            worker = new NioEventLoopGroup();
            bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(worker);
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5 * 1000);
            bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
                @Override
                protected void initChannel(NioSocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
//                    pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                    pipeline.addLast(new HttpClientCodec());
                    pipeline.addLast(new ChunkedWriteHandler());
                    pipeline.addLast(new HttpObjectAggregator(65535));
                    pipeline.addLast(new WebSocketClientProtocolHandler(WebSocketClientHandshakerFactory.newHandshaker(
                            uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()
                    )));
                    pipeline.addLast(new IdleStateHandler(1, 8,0, TimeUnit.SECONDS));
                    pipeline.addLast(new ClientHandler());
                }
            });
            connect();

        } catch (Exception e){
            e.printStackTrace();
        } finally {
//            stop();
        }
    }

    public static void connect() throws Exception {
        Client.worker.scheduleWithFixedDelay(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                if(channelFuture!=null){
                    if(channelFuture.channel().isActive())
                        return;
                    channelFuture.channel().close().sync();
                    channelFuture = null;
                }

                log.info("开始连接:"+host+":"+port+" ...");
                RpcClientService.get().setConnStatus(ConnStatus.CONNECTING);
                try {
                    channelFuture = bootstrap.connect(host, port).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if(future.isSuccess()){
                                log.info("连接成功！");
                                RpcClientService.get().setChannel(channelFuture.channel());
                                RpcClientService.get().setConnStatus(ConnStatus.ON_LINE);
                            } else {
                                log.warn("连接失败, "+Client.DELAY+"ms后重试");
                            }
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, Client.DELAY, TimeUnit.MILLISECONDS);

    }

    public static void stop(){
        worker.shutdownGracefully();
    }
}
