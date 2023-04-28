package com.tjmfreeone.tjmrpc.client.netty;

import com.tjmfreeone.tjmrpc.client.RpcClientService;
import com.tjmfreeone.tjmrpc.client.common.*;
import com.tjmfreeone.tjmrpc.client.connection.ConnStatus;
import com.tjmfreeone.tjmrpc.client.message.BaseMsg;
import com.tjmfreeone.tjmrpc.client.message.MsgType;
import com.tjmfreeone.tjmrpc.client.message.recv.InvokeRequest;
import com.tjmfreeone.tjmrpc.client.message.send.Ping;
import com.tjmfreeone.tjmrpc.client.message.send.InvokeResponse;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;


import static com.tjmfreeone.tjmrpc.client.common.TheObjectMapper.*;

@Slf4j
@ChannelHandler.Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        RpcClientService.get().setInitState(false);
        RpcClientService.get().setConnStatus(ConnStatus.OFF_LINE);
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        BaseMsg baseMsg = OBJECT_MAPPER.readValue(msg.text(), BaseMsg.class);
        log.info(baseMsg.toString());

        if(baseMsg.getMsgType() == MsgType.InvokeRequest){
            InvokeRequest invokeRequest = (InvokeRequest) baseMsg;
            Function function = RpcClientService.get().getFunction(invokeRequest.getFunctionId());

            if(function!=null){
                if (function instanceof FunctionReact){
                    FunctionReact targetFunction = (FunctionReact) function;
                    InvokeResponse invokeResponse = new InvokeResponse();
                    invokeResponse.setRequestId(invokeRequest.getRequestId());
                    try {
                        targetFunction.onInvoke(invokeRequest, invokeResponse);
                    } catch (Exception e){
                        invokeResponse.setFail(e.toString());
                    }
                    RpcClientService.get().getNettyClient().getChannel().writeAndFlush(new TextWebSocketFrame(OBJECT_MAPPER.writeValueAsString(invokeResponse))).sync();

                } else if (function instanceof FunctionDeferred){
                    FunctionDeferred targetFunction = (FunctionDeferred) function;
                    String taskId = invokeRequest.getParamKeyValues().getOrDefault("taskId", null);
                    if(taskId==null){
                        InvokeResponse invokeResponse = new InvokeResponse();
                        invokeResponse.setRequestId(invokeRequest.getRequestId());
                        invokeResponse.setFail("calling deferred function, but missing parameter: taskId");
                        RpcClientService.get().getNettyClient().getChannel().writeAndFlush(new TextWebSocketFrame(OBJECT_MAPPER.writeValueAsString(invokeResponse))).sync();
                        return;
                    }
                    DeferredTask deferredTask = new DeferredTask(taskId, invokeRequest.getRequestId());
                    DeferredTaskManager.get().addTask(deferredTask);
                    try {
                        targetFunction.invokeAndGetDeferredResult(invokeRequest);
                    } catch (Exception e){
                        deferredTask.getDeferredObject().reject(e);
                    }
                }
            }
        }
        else if (baseMsg.getMsgType() == MsgType.AckInitMsg) {
            RpcClientService.get().setInitState(true);
            RpcClientService.get().setConnStatus(ConnStatus.ON_LINE);
        }
        ctx.flush();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if(IdleState.READER_IDLE.equals(idleStateEvent.state()) && !RpcClientService.get().hasInited()){
                RpcClientService.get().sendInitMsg();
            }
            if(IdleState.WRITER_IDLE.equals(idleStateEvent.state())){
                Ping ping = new Ping();
                ping.setClientId(RpcClientService.get().getClientId());
                ctx.channel().writeAndFlush(new TextWebSocketFrame(OBJECT_MAPPER.writeValueAsString(ping))).sync();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("异常发生:"+ cause.getMessage());
        ctx.close();
        RpcClientService.get().setInitState(false);
        RpcClientService.get().setConnStatus(ConnStatus.OFF_LINE);
    }
}