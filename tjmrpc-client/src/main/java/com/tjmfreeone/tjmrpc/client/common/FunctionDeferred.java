package com.tjmfreeone.tjmrpc.client.common;

import com.tjmfreeone.tjmrpc.client.message.recv.InvokeRequest;
import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class FunctionDeferred implements Function{

    @EqualsAndHashCode.Include()
    @NonNull
    private String functionId;

    @EqualsAndHashCode.Include()
    @NonNull
    private String requestMethod = "GET";

    public FunctionDeferred(String functionId){
        this.functionId = functionId;
    }


    public abstract void invokeAndGetDeferredResult(InvokeRequest request) throws Exception;

}
