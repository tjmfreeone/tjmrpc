package com.tjmfreeone.tjmrpc.client.netty;

import com.tjmfreeone.tjmrpc.client.RpcClientService;

import java.net.URI;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class Client {

    public static NioEventLoopGroup worker =  new NioEventLoopGroup();
    private static Bootstrap bootstrap;

    private static ChannelFuture channelFuture;

    private static String host = RpcClientService.get().getHost();
    private static int port = RpcClientService.get().getPort();

    public final static int DELAY = 5;


    public static void spawn(URI uri){
        try{
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
                    pipeline.addLast(new IdleStateHandler(0, 4,0, TimeUnit.SECONDS));
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
            @Override
            public void run() {
                if(channelFuture!=null&&channelFuture.channel().isActive())
                    return;
                log.info("开始连接:"+host+":"+port+" ...");
                try {
                    channelFuture = bootstrap.connect(host, port).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if(future.isSuccess()){
                                log.info("连接成功！");
                                RpcClientService.get().setChannel(channelFuture.channel());
                            } else {
                                log.warn("连接失败, "+Client.DELAY+"s后重试");
                            }
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, Client.DELAY, TimeUnit.SECONDS);

    }
}
