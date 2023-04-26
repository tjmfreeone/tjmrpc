package com.tjmfreeone.tjmrpc.client.common;

import com.tjmfreeone.tjmrpc.client.message.recv.InvokeRequest;
import com.tjmfreeone.tjmrpc.client.message.send.InvokeResponse;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public abstract class FunctionReact extends Function{

    @EqualsAndHashCode.Include()
    @NonNull
    private String functionId;

    @EqualsAndHashCode.Include()
    @NonNull
    private ReqMethod reqMethod = ReqMethod.GET;

    public FunctionReact(String functionId){
        this.functionId = functionId;
    }

    public abstract void onInvoke(InvokeRequest request, InvokeResponse response) throws Exception;


}
