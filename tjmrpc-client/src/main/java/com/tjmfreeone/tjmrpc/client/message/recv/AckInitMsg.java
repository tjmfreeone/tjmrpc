package com.tjmfreeone.tjmrpc.client.message.recv;

import com.tjmfreeone.tjmrpc.client.message.BaseMsg;
import com.tjmfreeone.tjmrpc.client.message.MsgType;
import lombok.*;

@Data
@ToString
public class AckInitMsg extends BaseMsg {
    private final MsgType msgType = MsgType.AckInitMsg;

}
