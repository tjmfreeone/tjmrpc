package com.tjmfreeone.tjmrpc.client;

import com.tjmfreeone.tjmrpc.client.common.Function;
import com.tjmfreeone.tjmrpc.client.message.send.InitMsg;
import com.tjmfreeone.tjmrpc.client.netty.Client;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static com.tjmfreeone.tjmrpc.client.common.TheObjectMapper.OBJECT_MAPPER;
@Setter
@Getter
@Slf4j
public class RpcClientService {
    private static volatile RpcClientService INSTANCE;


    @NonNull
    private String host;
    @NonNull
    private Integer port;
    @NonNull
    private String bucketId;
    @NonNull
    private String clientId;

    @NonNull
    private Channel channel;

    @NonNull
    private static volatile Map<String, Function> functions;

    public static boolean hasInited = false;

    private RpcClientService(){
        if(INSTANCE!=null){
            throw new RuntimeException("单例已被创建"); // 防止反射破坏
        }
    }

    public static RpcClientService get(){
        if(INSTANCE==null){
            synchronized (RpcClientService.class){
                if(INSTANCE==null){
                    INSTANCE = new RpcClientService();
                    functions = new ConcurrentHashMap<String, Function>();
                }
            }
        }
        return INSTANCE;
    }


    public void registerFunction(Function function) throws Exception {
        functions.put(function.getFunctionId(), function);
    }

    public void unregisterFunction(Function function){
        functions.remove(function.getFunctionId());
    }

    public Function getFunction(String functionId){
        return functions.getOrDefault(functionId, null);
    }

    public void start_connect() throws InterruptedException {
        URI uri = URI.create("ws://" + host + ":" + port + "/tjmrpc/websocket");
        Client.spawn(uri);
    }

    public void sendInitMsg() throws Exception{
        InitMsg initMsg = new InitMsg();
        initMsg.setBucketId(bucketId);
        initMsg.setClientId(clientId);
        initMsg.setFunctions(functions);
        log.info(initMsg.toString());
        ChannelFuture channelFuture = channel.writeAndFlush(new TextWebSocketFrame(OBJECT_MAPPER.writeValueAsString(initMsg)));
        channelFuture.sync();
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.info("发送成功！！");
                hasInited = true;
            }
        });
    }

}
