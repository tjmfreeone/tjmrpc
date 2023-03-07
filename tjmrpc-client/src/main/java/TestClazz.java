import com.fasterxml.jackson.databind.JsonNode;
import com.tjmfreeone.tjmrpc.client.RpcClientService;
import com.tjmfreeone.tjmrpc.client.common.*;
import com.tjmfreeone.tjmrpc.client.message.recv.InvokeRequest;
import com.tjmfreeone.tjmrpc.client.message.send.InvokeResponse;

import java.util.UUID;

public class TestClazz {
    public static void main(String[] args) throws Exception {
        RpcClientService.get().setHost("127.0.0.1");
        RpcClientService.get().setPort(7788);
        RpcClientService.get().setBucketId("appExample");
        RpcClientService.get().setClientId(UUID.randomUUID().toString());

        RpcClientService.get().registerFunction(new FunctionReact("getSign", "GET") {
            @Override
            public void onInvoke(InvokeRequest request, InvokeResponse response) throws Exception{
                String result = "";
//                JsonNode invokeBody = request.getInvokeBody();
                result += request.getParamKeyValues().get("data");

                response.setSuccess(result);
            }
        });
        RpcClientService.get().registerFunction(new FunctionDeferred("getSign2", "GET") {
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
