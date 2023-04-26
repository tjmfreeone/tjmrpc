package com.tjmfreeone.tjmrpc.client.common;

import com.tjmfreeone.tjmrpc.client.RpcClientService;
import com.tjmfreeone.tjmrpc.client.message.send.InvokeResponse;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import org.jdeferred2.DoneCallback;
import org.jdeferred2.FailCallback;
import org.jdeferred2.impl.DeferredObject;

import static com.tjmfreeone.tjmrpc.client.common.TheObjectMapper.OBJECT_MAPPER;


@Getter
@Setter
public class DeferredTask {
    private DeferredObject deferredObject;
    private InvokeResponse invokeResponse;

    public String id;

    public String requestId;

    @SneakyThrows
    public DeferredTask(String id, String requestId) {
        this.id = id;
        this.invokeResponse = new InvokeResponse();
        this.invokeResponse.setRequestId(requestId);
        initDeferredObject();
    }

    public void initDeferredObject() throws Exception{
        deferredObject = new DeferredObject<>();
        deferredObject.promise().done(new DoneCallback() {
            @SneakyThrows
            @Override
            public void onDone(Object o) {
                invokeResponse.setSuccess(o);
                DeferredTaskManager.get().removeAnyway(id);
                RpcClientService.get().getNettyClient().getChannel().writeAndFlush(new TextWebSocketFrame(OBJECT_MAPPER.writeValueAsString(invokeResponse))).sync();
            }
        }).fail(new FailCallback() {
            @SneakyThrows
            @Override
            public void onFail(Object o) {
                invokeResponse.setFail(o);
                DeferredTaskManager.get().removeAnyway(id);
                RpcClientService.get().getNettyClient().getChannel().writeAndFlush(new TextWebSocketFrame(OBJECT_MAPPER.writeValueAsString(invokeResponse))).sync();
            }
        });
    }

    public boolean isFinished(){
        return deferredObject.isRejected() || deferredObject.isResolved();
    }


}
