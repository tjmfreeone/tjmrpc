package com.tjmfreeone.tjmrpc.client.message.send;

import com.tjmfreeone.tjmrpc.client.message.BaseMsg;
import com.tjmfreeone.tjmrpc.client.message.MsgType;

import lombok.*;



@Data
@Getter
@ToString
@EqualsAndHashCode(callSuper=false)
public class InvokeResponse extends BaseMsg {
    private final MsgType msgType = MsgType.InvokeResponse;

    private String requestId;

    private Object rawData;
    private String status;

    public InvokeResponse(){};

    public void setSuccess(Object result){
        rawData = result;
        status = "success";
    }

    public void setFail(Object reason){
        rawData = reason;
        status = "fail";
    }

}
