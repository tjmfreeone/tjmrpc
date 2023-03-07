package com.tjmfreeone.tjmrpc.server.message.recv;

import com.tjmfreeone.tjmrpc.server.message.BaseMsg;
import com.tjmfreeone.tjmrpc.server.message.MsgType;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@Getter
@ToString
public class InitMsg extends BaseMsg {
    private final MsgType msgType = MsgType.InitMsg;
    private String clientId;
    private String bucketId;
    private Set<String> functionIds;

    public InitMsg(){
        functionIds = new HashSet<String>();
    }

    public void addFunctionId(String functionId){
        functionIds.add(functionId);
    }

    public void removeFunctionId(String functionId){
        functionIds.remove(functionId);
    }
}
