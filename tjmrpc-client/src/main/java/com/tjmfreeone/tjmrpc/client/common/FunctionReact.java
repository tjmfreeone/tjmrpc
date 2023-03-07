package com.tjmfreeone.tjmrpc.client.common;

import com.tjmfreeone.tjmrpc.client.message.recv.InvokeRequest;
import com.tjmfreeone.tjmrpc.client.message.send.InvokeResponse;

import lombok.*;

@Data
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class FunctionReact implements Function{

    @EqualsAndHashCode.Include()
    @NonNull
    private String functionId;

    @EqualsAndHashCode.Include()
    @NonNull
    private String requestMethod = "GET";

    public FunctionReact(String functionId){
        this.functionId = functionId;
    }

    public abstract void onInvoke(InvokeRequest request, InvokeResponse response) throws Exception;


}
