package com.tjmfreeone.tjmrpc.server.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.tjmfreeone.tjmrpc.server.reponse.RespStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Setter
@Getter
public class InvokeEvent {
    private DeferredResult<RespStatus> deferredResult;
    private String bucketId;
    private String functionId;
    private Map<String, String> paramKeyValues;

    private JsonNode invokeBody;

    public InvokeEvent(){
        paramKeyValues = new ConcurrentHashMap<String, String>();
    }

    public void addFunctionParamKeyValues(String paramKey, String paramValue){
        paramKeyValues.put(paramKey, paramValue);
    }

    public void removeFunctionParamKeyValues(String paramKey){
        paramKeyValues.remove(paramKey);
    }
}
