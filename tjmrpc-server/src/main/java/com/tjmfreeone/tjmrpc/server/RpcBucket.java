//
//rpcBucket , 多个拥有相同功能的rpcClient即为一个bucket
//
package com.tjmfreeone.tjmrpc.server;

import com.tjmfreeone.tjmrpc.server.common.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RpcBucket {
    public String bucketId;
     public ConcurrentHashMap<String, RpcClient> clientMap;
    public ConcurrentHashMap<String, Function> functions;

    public int currPos = 0;

    public RpcBucket(String bucketId){
        this.bucketId = bucketId;
        this.clientMap = new ConcurrentHashMap<>();
        this.functions = new ConcurrentHashMap<String, Function>();
    }

    public void addClient(RpcClient rpcClient){
        clientMap.put(rpcClient.getClientId(), rpcClient);
    }

    public void removeClient(String clientId){
        if(clientMap.containsKey(clientId)){
            clientMap.remove(clientId);
        }
    }

    public int clientCount(){
        return clientMap.size();
    }

    public List<String> getAllClientIds(){
        List<String> clientIds = new ArrayList<String>(clientMap.keySet());
        return clientIds;
    }

    public RpcClient loopGetRpcClient(){
        String clientId;
        List<String> clientIds = new ArrayList<String>(clientMap.keySet());
        if(currPos < clientIds.size()){
            clientId = clientIds.get(currPos);
        }else {
            clientId = clientIds.get(0);
            currPos = 0;
        }
        currPos += 1;
        return clientMap.get(clientId);
    }

    public void registerFunction(Function function){
        functions.put(function.getFunctionId(), function);
    }

    public Function getFunction(String functionId){
        return functions.getOrDefault(functionId, null);
    }

    public boolean containsFunction(String functionId){
        return functions.containsKey(functionId);
    }

    public boolean containsClient(String clientId){
        return clientMap.containsKey(clientId);
    }

    public void clearAll(){
        clientMap.clear();
        functions.clear();
    }

    @Override
    public String toString() {
        return "RpcBucket{" +
                "bucketId='" + bucketId + '\'' +
                ", clientMap=" + clientMap +
                '}';
    }
}
