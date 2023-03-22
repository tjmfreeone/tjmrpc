import com.fasterxml.jackson.databind.JsonNode;
import com.tjmfreeone.tjmrpc.client.RpcClientService;
import com.tjmfreeone.tjmrpc.client.common.*;
import com.tjmfreeone.tjmrpc.client.message.recv.InvokeRequest;
import com.tjmfreeone.tjmrpc.client.message.send.InvokeResponse;

import java.util.UUID;

public class TestClazz {
    public static void main(String[] args) throws Exception {
        RpcClientService.get().setHost("192.168.1.213");
        RpcClientService.get().setPort(7788);
        RpcClientService.get().setBucketId("appExample");
        RpcClientService.get().setClientId(UUID.randomUUID().toString());
        RpcClientService.get().setSchedulerDelay(1000);

        RpcClientService.get().registerFunction(new FunctionReact("getSign", ReqMethod.POST) {
            @Override
            public void onInvoke(InvokeRequest request, InvokeResponse response) throws Exception{
                String result = "";
                JsonNode invokeBody = request.getInvokeBody();
                result += invokeBody.toString();

                response.setSuccess(result);
            }
        });
        RpcClientService.get().registerFunction(new FunctionDeferred("getSign2", ReqMethod.GET) {
            @Override
            public void invokeAndGetDeferredResult(InvokeRequest request) throws Exception {
                System.out.println("异步调用了");
                DeferredTask deferredTask = DeferredTaskManager.get().getTask(request.getParamKeyValues().get("taskId"));
                System.out.println(deferredTask.toString());
                if(deferredTask!=null)
                    Thread.sleep(200);
                    deferredTask.getDeferredObject().resolve("异步调用成功了！！！");
            }
        });
        RpcClientService.get().start_connect();

    }
}
