package com.tjmfreeone.tjmrpc.server.netty;

import com.tjmfreeone.tjmrpc.server.RpcBucket;
import com.tjmfreeone.tjmrpc.server.RpcClient;
import com.tjmfreeone.tjmrpc.server.RpcContainerManager;
import com.tjmfreeone.tjmrpc.server.DeferredRequestManager;
import com.tjmfreeone.tjmrpc.server.common.Function;
import com.tjmfreeone.tjmrpc.server.message.BaseMsg;
import com.tjmfreeone.tjmrpc.server.message.recv.InitMsg;
import com.tjmfreeone.tjmrpc.server.message.recv.InvokeResponse;
import com.tjmfreeone.tjmrpc.server.message.MsgType;
import com.tjmfreeone.tjmrpc.server.message.send.AckInitMsg;
import com.tjmfreeone.tjmrpc.server.message.send.Pong;
import com.tjmfreeone.tjmrpc.server.reponse.RespError;
import com.tjmfreeone.tjmrpc.server.reponse.RespStatus;
import com.tjmfreeone.tjmrpc.server.reponse.RespSuccess;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;

import static com.tjmfreeone.tjmrpc.server.common.TheObjectMapper.*;

@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    private AttributeKey<String> bucketIdKey = AttributeKey.valueOf("bucketId");
    private AttributeKey<String> clientIdKey = AttributeKey.valueOf("clientId");


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        BaseMsg baseMsg = OBJECT_MAPPER.readValue(msg.text(), BaseMsg.class);
        log.info((baseMsg.getMsgType().equals(MsgType.Ping)?"心跳":"收到消息")+":{}", baseMsg);
        if(baseMsg.getMsgType().equals(MsgType.Ping)){
            ctx.channel().writeAndFlush(new TextWebSocketFrame(OBJECT_MAPPER.writeValueAsString(new Pong()))).sync();
        }else if(baseMsg.getMsgType().equals(MsgType.InitMsg)){
            InitMsg initMsg = (InitMsg) baseMsg;
            String bucketId = initMsg.getBucketId();
            String clientId = initMsg.getClientId();
            Map<String, Function> functions = initMsg.getFunctions();
            if(bucketId!=null && clientId!=null && functions!=null){
                //////////////////////////////对bucket, client实例化处理///////////////////////////////
                RpcBucket rpcBucket;
                if (!RpcContainerManager.get().containsBucket(bucketId)) {
                    rpcBucket = new RpcBucket(bucketId);
                } else {
                    rpcBucket = RpcContainerManager.get().getBucketById(bucketId);
                }

                RpcClient rpcClient = new RpcClient.RpcClientBuilder().
                        bucketId(bucketId).
                        clientId(clientId).
                        channel(ctx.channel()).build();

                /////////////////////////////根据channel的id, 更新channel的属性, 避免出现两个client拥有同一个channel//////////////////
                Attribute<String> bucketIdAttr = ctx.channel().attr(bucketIdKey);
                Attribute<String> clientIdAttr = ctx.channel().attr(clientIdKey);

                if(bucketIdAttr.get()!=null && clientIdAttr.get()!=null)
                    RpcContainerManager.get().getBucketById(bucketIdAttr.get()).removeClient(clientIdAttr.get());
                bucketIdAttr.set(rpcClient.getBucketId());
                clientIdAttr.set(rpcClient.getClientId());
                ////////////////////////////////将client,bucket加到manager里//////////////////////////////////////////////////
                if (!rpcBucket.containsClient(clientId)) {
                    rpcBucket.addClient(rpcClient);
                }
                if (!RpcContainerManager.get().containsBucket(bucketId)) {
                    RpcContainerManager.get().addBucket(rpcBucket);
                }

                for (Function function : functions.values()) {
                    rpcBucket.registerFunction(function);
                }
            }
            ctx.channel().writeAndFlush(new TextWebSocketFrame(OBJECT_MAPPER.writeValueAsString(new AckInitMsg()))).sync();
        }else if(baseMsg.getMsgType().equals(MsgType.InvokeResponse)){
            InvokeResponse invokeResult = (InvokeResponse) baseMsg;
            String requestId = invokeResult.getRequestId();
            Attribute<String> clientIdAttr = ctx.channel().attr(clientIdKey);
            String clientId = clientIdAttr.get();
            if(DeferredRequestManager.get().hasRequestId(requestId)){
                DeferredResult<RespStatus> deferredResult = DeferredRequestManager.get().pop(requestId);
                if(deferredResult==null)return;
                switch (invokeResult.getStatus()){
                    case "success":
                        deferredResult.setResult(new RespSuccess(invokeResult.getRawData(),  clientId));
                        break;
                    case "fail":
                        deferredResult.setResult(new RespError(invokeResult.getRawData().toString(), clientId));
                        break;
                    default:
                        deferredResult.setResult(new RespError("unknown"));
                        break;
                }
            }
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if(IdleState.READER_IDLE.equals(idleStateEvent.state())){
                // 一定时间后没收到消息, 触发读空闲事件
                ctx.channel().close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Attribute<String> bucketIdAttr = ctx.channel().attr(bucketIdKey);
        Attribute<String> clientIdAttr = ctx.channel().attr(clientIdKey);
        String bucketId = bucketIdAttr.get();
        String clientId = clientIdAttr.get();
        if(bucketId!=null && clientId!=null){
            log.info("移除bucketId:{}, clientId:{}", bucketId, clientId);
            RpcBucket rpcBucket = RpcContainerManager.get().getBucketById(bucketId);
            if(rpcBucket!=null){
                rpcBucket.removeClient(clientId);
                if(rpcBucket.clientCount()==0){
                    RpcContainerManager.get().removeBucket(bucketId);
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("{} 异常发生:{}", ctx.channel().id(), cause);
        ctx.close();
    }
}
