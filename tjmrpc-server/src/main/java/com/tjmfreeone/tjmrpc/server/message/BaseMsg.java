package com.tjmfreeone.tjmrpc.server.message;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tjmfreeone.tjmrpc.server.message.recv.InitMsg;
import com.tjmfreeone.tjmrpc.server.message.recv.InvokeResponse;
import com.tjmfreeone.tjmrpc.server.message.recv.Ping;
import com.tjmfreeone.tjmrpc.server.message.send.InvokeRequest;
import com.tjmfreeone.tjmrpc.server.message.send.Pong;
import com.tjmfreeone.tjmrpc.server.message.send.AckInitMsg;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "msgType", visible = true)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = InitMsg.class, name = "InitMsg"),
        @JsonSubTypes.Type(value = AckInitMsg.class, name = "AckInitMsg"),
        @JsonSubTypes.Type(value = InvokeResponse.class, name = "InvokeResponse"),
        @JsonSubTypes.Type(value = InvokeRequest.class, name = "InvokeRequest"),
        @JsonSubTypes.Type(value = Ping.class, name = "Ping"),
        @JsonSubTypes.Type(value = Pong.class, name = "Pong")
})
public class BaseMsg {

    MsgType msgType;
}
