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
        return "InvokeResponse(" +
                "msgType=" + msgType +
                ", requestId='" + requestId + '\'' +
                ", rawData=" + rawData.toString().substring(0, 64) + "..." +
                ", status='" + status + '\'' +
                ')';
    }
}
