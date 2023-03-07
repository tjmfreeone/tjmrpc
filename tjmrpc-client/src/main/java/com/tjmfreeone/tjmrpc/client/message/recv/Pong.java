package com.tjmfreeone.tjmrpc.client.message.recv;

import com.tjmfreeone.tjmrpc.client.message.BaseMsg;
import com.tjmfreeone.tjmrpc.client.message.MsgType;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Pong extends BaseMsg {
    private final MsgType msgType = MsgType.Pong;
}
