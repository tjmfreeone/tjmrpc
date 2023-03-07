package com.tjmfreeone.tjmrpc.server.message.send;

import com.fasterxml.jackson.databind.JsonNode;
import com.tjmfreeone.tjmrpc.server.message.BaseMsg;
import com.tjmfreeone.tjmrpc.server.message.MsgType;
import lombok.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@Getter
@Setter
public class InvokeRequest extends BaseMsg {
    private final MsgType msgType = MsgType.InvokeRequest;
    private String bucketId;
    private String clientId;
    private String functionId;
    private String requestId;
    private Map<String, String> paramKeyValues;

    private JsonNode invokeBody;

    public InvokeRequest() {
        paramKeyValues = new ConcurrentHashMap<String, String>();
    }

}