package com.tjmfreeone.tjmrpc.client.message.send;

import com.tjmfreeone.tjmrpc.client.common.Function;
import com.tjmfreeone.tjmrpc.client.message.BaseMsg;
import com.tjmfreeone.tjmrpc.client.message.MsgType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.*;

@Data
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper=false)
public class InitMsg extends BaseMsg {
    private final MsgType msgType = MsgType.InitMsg;
    private String clientId;
    private String bucketId;

    private Map<String, Function> functions;

    public InitMsg(){
        functions = new HashMap<>();
    }

    public void addFunction(Function function){
        functions.put(function.getFunctionId(), function);
    }

    public void removeFunctionId(String functionId){
        functions.remove(functionId);
    }

}
