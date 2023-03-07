package com.tjmfreeone.tjmrpc.client.common;

import com.tjmfreeone.tjmrpc.client.message.recv.InvokeRequest;
import com.tjmfreeone.tjmrpc.client.message.send.InvokeResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Data
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class FunctionReact implements Function{

    @EqualsAndHashCode.Include()
    @NonNull
    private String functionId;

    public abstract void onInvoke(InvokeRequest request, InvokeResponse response) throws Exception;


}
