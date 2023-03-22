package com.tjmfreeone.tjmrpc.server.message.recv;

import com.tjmfreeone.tjmrpc.server.message.BaseMsg;
import com.tjmfreeone.tjmrpc.server.message.MsgType;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@Getter
public class InvokeResponse extends BaseMsg {
    private final MsgType msgType = MsgType.InvokeResponse;
    private String requestId;
    private Object rawData;
    private String status;

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
