package com.tjmfreeone.tjmrpc.server.message.recv;

import com.tjmfreeone.tjmrpc.server.message.BaseMsg;
import com.tjmfreeone.tjmrpc.server.message.MsgType;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Ping extends BaseMsg {
    private final MsgType msgType = MsgType.Ping;
    private String clientId;
}
