package com.tjmfreeone.tjmrpc.server.message.recv;

import com.tjmfreeone.tjmrpc.server.common.Function;
import com.tjmfreeone.tjmrpc.server.message.BaseMsg;
import com.tjmfreeone.tjmrpc.server.message.MsgType;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Data
@Getter
@ToString
public class InitMsg extends BaseMsg {
    private final MsgType msgType = MsgType.InitMsg;
    private String clientId;
    private String bucketId;
    private Map<String, Function> functions;

    public InitMsg(){
        functions = new HashMap<>();
    }

    public void addFunctionId(Function function){
        functions.put(function.getFunctionId(), function);
    }

    public void removeFunction(String functionId){
        functions.remove(functionId);
    }
}
