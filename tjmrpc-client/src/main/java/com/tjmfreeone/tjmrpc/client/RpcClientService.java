package com.tjmfreeone.tjmrpc.client;

import com.tjmfreeone.tjmrpc.client.connection.ConnStatus;
import com.tjmfreeone.tjmrpc.client.common.Function;
import com.tjmfreeone.tjmrpc.client.connection.IConnStatusListener;
import com.tjmfreeone.tjmrpc.client.message.send.InitMsg;
import com.tjmfreeone.tjmrpc.client.netty.NettyClient;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    private static volatile Map<String, Function> functions;

    @Getter
    private ConnStatus connStatus = ConnStatus.OFF_LINE;

    private List<IConnStatusListener> connStatusListeners = new LinkedList<>();


    private NettyClient nettyClient;


    private boolean isInited = false;

    private boolean isActivate = false;

    public URI getUri(){
        return URI.create("ws://" + host + ":" + port + "/tjmrpc/websocket");
    }

    private RpcClientService(){
        if(INSTANCE!=null){
            throw new RuntimeException("单例已被创建"); // 防止反射破坏
        }
    }

    public void activate(){
        isActivate = true;
        nettyClient = new NettyClient(host, port, getUri(), 1000L);
        nettyClient.prepare4connect();
        nettyClient.loopRunConnect();
    }

    public void deactivate(){
        isActivate = false;
        nettyClient.stopLoopConnect();
        this.setConnStatus(ConnStatus.OFF_LINE);
        this.resetInitState();
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
        this.isInited = false;
    }

    public void unregisterFunction(Function function){
        functions.remove(function.getFunctionId());
    }

    public Function getFunction(String functionId){
        return functions.getOrDefault(functionId, null);
    }


    public void addConnStatusListener(IConnStatusListener listener){
        if(!connStatusListeners.contains(listener)){
            connStatusListeners.add(listener);
        }
    }

    public void clearConnStatusListener(){
        connStatusListeners.clear();
    }

    public void setConnStatus(ConnStatus status){
        this.connStatus = status;
        notifyStatus(status);
        System.out.println("ConnStatusChange:"+status);
    }

    public void notifyStatus(ConnStatus status){
        for(IConnStatusListener listener: connStatusListeners){
            listener.onStatusChange(status);
        }
    }


    public void sendInitMsg() throws Exception{
        InitMsg initMsg = new InitMsg();
        initMsg.setBucketId(bucketId);
        initMsg.setClientId(clientId);
        initMsg.setFunctions(functions);
        log.info(initMsg.toString());
        ChannelFuture channelFuture = nettyClient.getChannel().writeAndFlush(new TextWebSocketFrame(OBJECT_MAPPER.writeValueAsString(initMsg)));
        channelFuture.sync();
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.info("发送成功！！");
                isInited = true;
            }
        });
    }


    public void resetInitState(){
        isInited = false;
    }

    public boolean hasInited(){
        return isInited;
    }

}
