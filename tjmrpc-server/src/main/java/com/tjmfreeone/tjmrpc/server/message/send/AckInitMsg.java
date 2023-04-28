package com.tjmfreeone.tjmrpc.server.message.send;

import com.tjmfreeone.tjmrpc.server.message.BaseMsg;
import com.tjmfreeone.tjmrpc.server.message.MsgType;
import lombok.*;

@Data
@ToString
public class AckInitMsg extends BaseMsg {
    private final MsgType msgType = MsgType.AckInitMsg;

}
