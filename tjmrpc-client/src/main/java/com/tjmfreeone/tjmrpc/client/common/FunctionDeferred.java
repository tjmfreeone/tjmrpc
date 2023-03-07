package com.tjmfreeone.tjmrpc.client.common;

import com.tjmfreeone.tjmrpc.client.message.recv.InvokeRequest;
import com.tjmfreeone.tjmrpc.client.message.send.InvokeResponse;
import lombok.*;
import org.jdeferred2.impl.DeferredObject;

@Data
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class FunctionDeferred implements Function{

    @EqualsAndHashCode.Include()
    @NonNull
    private String functionId;


    public abstract void invokeAndGetDeferredResult(InvokeRequest request) throws Exception;

}
