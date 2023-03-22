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

    @Override
    public String toString() {
        String show_data;
        if(rawData.toString().length() > 64)
        {
            show_data = rawData.toString().substring(0, 64) + "...";
        }else{
            show_data = rawData.toString();
        }
        return "InvokeResponse(" +
                "msgType=" + msgType +
                ", requestId='" + requestId + '\'' +
                ", rawData=" + show_data +
                ", status='" + status + '\'' +
                ')';
    }

}
