package com.tjmfreeone.tjmrpc.client.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.tjmfreeone.tjmrpc.client.message.recv.InvokeRequest;
import com.tjmfreeone.tjmrpc.client.message.recv.Pong;
import com.tjmfreeone.tjmrpc.client.message.send.InitMsg;
import com.tjmfreeone.tjmrpc.client.message.send.InvokeResponse;

import com.tjmfreeone.tjmrpc.client.message.send.Ping;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "msgType", visible = true)
@JsonSubTypes(value = {
        @JsonSubTypes.Type(value = InitMsg.class, name = "InitMsg"),
        @JsonSubTypes.Type(value = InvokeResponse.class, name = "InvokeResponse"),
        @JsonSubTypes.Type(value = InvokeRequest.class, name = "InvokeRequest"),
        @JsonSubTypes.Type(value = Ping.class, name = "Ping"),
        @JsonSubTypes.Type(value = Pong.class, name = "Pong")
})
public class BaseMsg {
    MsgType msgType;
}
