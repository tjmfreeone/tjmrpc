package com.tjmfreeone.tjmrpc.client.message.send;

import com.tjmfreeone.tjmrpc.client.message.BaseMsg;
import com.tjmfreeone.tjmrpc.client.message.MsgType;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Ping extends BaseMsg {
    private final MsgType msgType = MsgType.Ping;
    private String clientId;
}
