package com.tjmfreeone.tjmrpc.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.tjmfreeone.tjmrpc.server.RpcBucket;
import com.tjmfreeone.tjmrpc.server.RpcContainerManager;
import com.tjmfreeone.tjmrpc.server.event.InvokeEvent;
import com.tjmfreeone.tjmrpc.server.reponse.*;
import com.tjmfreeone.tjmrpc.server.utils.DefaultVal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;

@RestController
@RequestMapping("/tjmrpc")
public class MainController {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @GetMapping(value = "/getBucketCount")
    public Object getBucketCount(){
        return new RespSuccess(RpcContainerManager.get().bucketCount());
    }

    @GetMapping(value = "/getBuckets")
    public Object getBuckets() throws JsonProcessingException {
        return new RespSuccess(RpcContainerManager.get().getAllBucketIds());
    }

    @GetMapping(value = "/getClients", params = {"bucketId"})
    public Object getClients(String bucketId){
        return new RespSuccess(RpcContainerManager.get().getClientsByBucketId(bucketId));
    }

    @GetMapping(value = "/getFunctions", params = {"bucketId"})
    public Object getFunctions(String bucketId){
        RpcBucket rpcBucket = RpcContainerManager.get().getBucketById(bucketId);
        return new RespSuccess(rpcBucket.functions.values());
    }

    @GetMapping(value = "/getClientCount", params = {"bucketId"})
    public Object getClientCount(String bucketId){
        return new RespSuccess(RpcContainerManager.get().getClientsByBucketId(bucketId).size());
    }

    @RequestMapping(value = "/invoke", method = {RequestMethod.GET, RequestMethod.POST}, params = {"bucketId", "functionId"})
    public DeferredResult<RespStatus> invoke(String bucketId, String functionId, RequestMethod requestMethod,
                                             @RequestParam(value="timeout", required=false)Long timeout,
                                             @RequestParam Map<String,String> paramKeyValues,
                                             @RequestBody(required = false) JsonNode invokeBody){

        DeferredResult<RespStatus> deferredResult = new DeferredResult<>( timeout!=null && timeout>0?timeout:DefaultVal.INVOKE_TIME_OUT, new RespTimeOut());

        paramKeyValues.remove("bucketId");
        paramKeyValues.remove("functionId");
        InvokeEvent invokeEvent = new InvokeEvent();
        invokeEvent.setBucketId(bucketId);
        invokeEvent.setFunctionId(functionId);
        invokeEvent.setDeferredResult(deferredResult);
        invokeEvent.setParamKeyValues(paramKeyValues);
        invokeEvent.setInvokeBody(invokeBody);
        applicationEventPublisher.publishEvent(invokeEvent);
        return deferredResult;
    }
}
