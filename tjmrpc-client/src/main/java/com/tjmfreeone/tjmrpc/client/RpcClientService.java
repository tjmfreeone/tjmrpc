package com.tjmfreeone.tjmrpc.client;

import com.tjmfreeone.tjmrpc.client.connection.ConnStatus;
import com.tjmfreeone.tjmrpc.client.common.Function;
import com.tjmfreeone.tjmrpc.client.connection.IConnStatusListener;
import com.tjmfreeone.tjmrpc.client.message.send.InitMsg;
import com.tjmfreeone.tjmrpc.client.netty.Client;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
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

    @Getter
    private ConnStatus connStatus;

    private List<IConnStatusListener> connStatusListeners = new LinkedList<>();

    @NonNull
    private static volatile Map<String, Function> functions;

    public boolean hasInited = false;

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


    public void registerFunction(Function function){
        functions.put(function.getFunctionId(), function);
        this.hasInited = false;
    }

    public void unregisterFunction(Function function){
        functions.remove(function.getFunctionId());
    }

    public Function getFunction(String functionId){
        return functions.getOrDefault(functionId, null);
    }

    public void setSchedulerDelay(long scheduler_delay){
        if(scheduler_delay>0) {
            Client.setDELAY(scheduler_delay);
        }
    }

    public void addConnStatusListener(IConnStatusListener listener){
        if(!connStatusListeners.contains(listener)){
            connStatusListeners.add(listener);
        }
    }

    public void setConnStatus(ConnStatus status){
        this.connStatus = status;
        notifyStatus(status);
    }

    public void notifyStatus(ConnStatus status){
        for(IConnStatusListener listener: connStatusListeners){
            listener.onStatusChange(status);
        }
    }

    public void start_connect(){
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

    public void close(){
        try {
            channel.close().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Client.stop();
        hasInited = false;
        this.setConnStatus(ConnStatus.OFF_LINE);
    }

}
