package com.tjmfreeone.tjmrpc.server.Listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.tjmfreeone.tjmrpc.server.RpcBucket;
import com.tjmfreeone.tjmrpc.server.RpcClient;
import com.tjmfreeone.tjmrpc.server.RpcContainerManager;
import com.tjmfreeone.tjmrpc.server.DeferredRequestManager;
import com.tjmfreeone.tjmrpc.server.common.ReqMethod;
import com.tjmfreeone.tjmrpc.server.event.InvokeEvent;
import com.tjmfreeone.tjmrpc.server.message.send.InvokeRequest;
import com.tjmfreeone.tjmrpc.server.reponse.RespError;
import com.tjmfreeone.tjmrpc.server.reponse.RespStatus;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static com.tjmfreeone.tjmrpc.server.common.TheObjectMapper.OBJECT_MAPPER;

@Slf4j
@Component
@EnableAsync
public class InvokeEventListener {

    @EventListener
    @Async
    public void process(InvokeEvent invokeEvent) throws InterruptedException, IOException {
        DeferredResult<RespStatus> deferredResult = invokeEvent.getDeferredResult();
        String bucketId = invokeEvent.getBucketId();
        String functionId = invokeEvent.getFunctionId();
        String clientId = invokeEvent.getClientId();
        Map<String, String> paramKeyValues = invokeEvent.getParamKeyValues();
        RpcBucket rpcBucket = RpcContainerManager.get().getBucketById(bucketId);

        JsonNode invokeBody = invokeEvent.getInvokeBody();


        if(rpcBucket==null){
            deferredResult.setResult(new RespError("no such bucketId"));
            return;
        }
        if(!rpcBucket.containsFunction(functionId)){
            deferredResult.setResult(new RespError("no such functionId in bucket " + bucketId));
            return;
        }

        if(rpcBucket.getFunction(functionId).getReqMethod().equals(ReqMethod.POST) && invokeBody==null){
            deferredResult.setResult(new RespError("found post body equals to null, please retry."));
            return;
        }

        if(RpcContainerManager.get().getBucketById(bucketId).clientCount()==0){
            deferredResult.setResult(new RespError("no client online in bucket " + bucketId));
            return;
        }

        RpcClient rpcClient = clientId!=null? rpcBucket.getTargetRpcClient(clientId): rpcBucket.loopGetRpcClient();
        if(rpcClient==null){
            deferredResult.setResult(new RespError("client not found."));
            return;
        }
        String requestId = UUID.randomUUID().toString();
        InvokeRequest invokeRequest = new InvokeRequest();
        invokeRequest.setRequestId(requestId);
        invokeRequest.setBucketId(bucketId);
        invokeRequest.setClientId(rpcClient.getClientId());
        invokeRequest.setFunctionId(functionId);
        invokeRequest.setParamKeyValues(paramKeyValues);
        invokeRequest.setInvokeBody(invokeEvent.getInvokeBody());

        ChannelFuture channelFuture = rpcClient.getChannel().writeAndFlush(new TextWebSocketFrame(OBJECT_MAPPER.writeValueAsString(invokeRequest)));

        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                DeferredRequestManager.get().add(requestId, deferredResult);
                deferredResult.onTimeout(new Runnable() {
                    @Override
                    public void run() {
                        DeferredRequestManager.get().removeAnyway(requestId);
                    }
                });
            }
        });
    }
}
