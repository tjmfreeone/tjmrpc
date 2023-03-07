package com.tjmfreeone.tjmrpc.client.message.send;

import com.tjmfreeone.tjmrpc.client.message.BaseMsg;
import com.tjmfreeone.tjmrpc.client.message.MsgType;

import java.util.HashSet;
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
