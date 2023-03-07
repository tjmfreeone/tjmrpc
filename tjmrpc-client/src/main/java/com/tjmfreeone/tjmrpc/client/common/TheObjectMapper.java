package com.tjmfreeone.tjmrpc.client.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.tjmfreeone.tjmrpc.client.message.BaseMsg;
import com.tjmfreeone.tjmrpc.client.message.recv.InvokeRequest;
import com.tjmfreeone.tjmrpc.client.message.recv.Pong;
import com.tjmfreeone.tjmrpc.client.message.send.InitMsg;
import com.tjmfreeone.tjmrpc.client.message.send.InvokeResponse;
import com.tjmfreeone.tjmrpc.client.message.send.Ping;

public class TheObjectMapper {
    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    static {
        //反序列化的时候如果多了其他属性,不抛出异常
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        //如果是空对象的时候,不抛异常
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        //属性为null不转换
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        OBJECT_MAPPER.addMixIn(BaseMsg.class, InitMsg.class);
        OBJECT_MAPPER.addMixIn(BaseMsg.class, Ping.class);
        OBJECT_MAPPER.addMixIn(BaseMsg.class, Pong.class);
        OBJECT_MAPPER.addMixIn(BaseMsg.class, InvokeRequest.class);
        OBJECT_MAPPER.addMixIn(BaseMsg.class, InvokeResponse.class);
    }

}
