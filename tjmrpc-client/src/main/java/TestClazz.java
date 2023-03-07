import com.tjmfreeone.tjmrpc.client.RpcClientService;
import com.tjmfreeone.tjmrpc.client.common.DeferredTask;
import com.tjmfreeone.tjmrpc.client.common.DeferredTaskManager;
import com.tjmfreeone.tjmrpc.client.common.FunctionDeferred;
import com.tjmfreeone.tjmrpc.client.common.FunctionReact;
import com.tjmfreeone.tjmrpc.client.message.BaseMsg;
import com.tjmfreeone.tjmrpc.client.message.recv.InvokeRequest;
import com.tjmfreeone.tjmrpc.client.message.recv.Pong;
import com.tjmfreeone.tjmrpc.client.message.send.InitMsg;
import com.tjmfreeone.tjmrpc.client.message.send.InvokeResponse;
import com.tjmfreeone.tjmrpc.client.message.send.Ping;

import java.util.UUID;

public class TestClazz {
    public static void main(String[] args) throws Exception {
        RpcClientService.get().setHost("127.0.0.1");
        RpcClientService.get().setPort(7788);
        RpcClientService.get().setBucketId("appExample");
        RpcClientService.get().setClientId(UUID.randomUUID().toString());

        RpcClientService.get().registerFunction(new FunctionReact("getSign") {
            @Override
            public void onInvoke(InvokeRequest request, InvokeResponse response) throws Exception{
                String result = "";
                for(String paramKey : request.getParamKeyValues().keySet()){
                    result += paramKey+"="+request.getParamKeyValues().get(paramKey) + "&";
                }

                response.setSuccess(result);
            }
        });
        RpcClientService.get().registerFunction(new FunctionDeferred("getSign2") {

            @Override
            public void invokeAndGetDeferredResult(InvokeRequest request) throws Exception {
                System.out.println("异步调用了");
                DeferredTask deferredTask = DeferredTaskManager.get().getTask("123");
                System.out.println(deferredTask.toString());
                if(deferredTask!=null)
                        deferredTask.getDeferredObject().resolve("异步调用成功了！！！");
            }
        });
        RpcClientService.get().start_connect();

    }
}
