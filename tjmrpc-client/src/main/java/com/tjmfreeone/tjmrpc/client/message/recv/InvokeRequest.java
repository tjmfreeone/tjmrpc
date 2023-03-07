package com.tjmfreeone.tjmrpc.client.message.recv;

import com.tjmfreeone.tjmrpc.client.message.BaseMsg;
import com.tjmfreeone.tjmrpc.client.message.MsgType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper=true)
public class InvokeRequest extends BaseMsg {
    private final MsgType msgType = MsgType.InvokeRequest;
    private String bucketId;
    private String clientId;
    private String functionId;
    private String requestId;

    private Map<String, String> paramKeyValues;

    public InvokeRequest() {
        paramKeyValues = new ConcurrentHashMap<String, String>();
    }
}
