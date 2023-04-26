package com.tjmfreeone.tjmrpc.client.common;

import com.tjmfreeone.tjmrpc.client.message.recv.InvokeRequest;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public abstract class FunctionDeferred extends Function{

    @EqualsAndHashCode.Include()
    @NonNull
    private String functionId;

    @EqualsAndHashCode.Include()
    @NonNull
    private ReqMethod reqMethod = ReqMethod.GET;

    public FunctionDeferred(String functionId){
        this.functionId = functionId;
    }


    public abstract void invokeAndGetDeferredResult(InvokeRequest request) throws Exception;

}
