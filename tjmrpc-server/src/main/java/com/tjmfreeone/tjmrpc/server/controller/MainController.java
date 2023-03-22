package com.tjmfreeone.tjmrpc.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.tjmfreeone.tjmrpc.server.RpcBucket;
import com.tjmfreeone.tjmrpc.server.RpcContainerManager;
import com.tjmfreeone.tjmrpc.server.event.InvokeEvent;
import com.tjmfreeone.tjmrpc.server.reponse.*;
import com.tjmfreeone.tjmrpc.server.utils.DefaultVal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;

@Api(tags = "操作")
@RestController
@RequestMapping("/tjmrpc")
public class MainController {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @ApiOperation(value = "", hidden = true)
    @GetMapping(value = "/getBucketCount")
    public Object getBucketCount(){
        return new RespSuccess(RpcContainerManager.get().bucketCount());
    }

    @ApiOperation(value = "bucket列表")
    @GetMapping(value = "/getBuckets")
    public Object getBuckets() throws JsonProcessingException {
        return new RespSuccess(RpcContainerManager.get().getAllBucketIds());
    }

    @ApiOperation(value = "客户端列表")
    @GetMapping(value = "/getClients", params = {"bucketId"})
    public Object getClients(String bucketId){
        return new RespSuccess(RpcContainerManager.get().getClientsByBucketId(bucketId));
    }

    @ApiOperation(value = "函数列表")
    @GetMapping(value = "/getFunctions", params = {"bucketId"})
    public Object getFunctions(String bucketId){
        RpcBucket rpcBucket = RpcContainerManager.get().getBucketById(bucketId);
        return new RespSuccess(rpcBucket.functions.values());
    }

    @ApiOperation(value = "", hidden = true)
    @GetMapping(value = "/getClientCount", params = {"bucketId"})
    public Object getClientCount(String bucketId){
        return new RespSuccess(RpcContainerManager.get().getClientsByBucketId(bucketId).size());
    }

    @ApiOperation(value = "调用")
    @RequestMapping(value = "/invoke", method = {RequestMethod.GET, RequestMethod.POST}, params = {"bucketId", "functionId"})
    public DeferredResult<RespStatus> invoke(String bucketId, String functionId, RequestMethod requestMethod,
                                             @RequestParam(value = "timeout", required = false) Long timeout,
                                             @RequestParam(value = "clientId", required = false) String clientId,
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
        if(clientId!=null){
            invokeEvent.setClientId(clientId);
        }
        applicationEventPublisher.publishEvent(invokeEvent);
        return deferredResult;
    }
}
