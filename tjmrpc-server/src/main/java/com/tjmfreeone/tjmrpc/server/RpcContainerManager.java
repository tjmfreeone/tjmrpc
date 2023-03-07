//
//rpc的容器管理, 包括bucket client
//
package com.tjmfreeone.tjmrpc.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;



public class RpcContainerManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static volatile RpcContainerManager INSTANCE; // volatile防止指令排
    private static volatile ConcurrentHashMap<String, RpcBucket> RpcBucketMap;


    private RpcContainerManager(){
        if(INSTANCE!=null){
            throw new RuntimeException("单例已被创建"); // 防止反射破坏
        }
    }

    public static RpcContainerManager get(){
        if(INSTANCE==null){
            synchronized (RpcContainerManager.class){
                if(INSTANCE==null){
                    INSTANCE = new RpcContainerManager();
                    RpcBucketMap = new ConcurrentHashMap<>();
                }
            }
        }
        return INSTANCE;
    }

    public int bucketCount(){
        return RpcBucketMap.size();
    }

    public List<String> getAllBucketIds(){
        List<String> bucketIds = new ArrayList<String>(RpcBucketMap.keySet());
        return bucketIds;
    }

    public List<String> getClientsByBucketId(String bucketId){
        if(RpcBucketMap.containsKey(bucketId) && RpcBucketMap.get(bucketId).clientCount()>0){
            return RpcBucketMap.get(bucketId).getAllClientIds();
        }
        return  new ArrayList<>();
    }

    public RpcBucket getBucketById(String bucketId){
        return RpcBucketMap.getOrDefault(bucketId, null);
    }

    public void addBucket(RpcBucket rpcBucket){
        RpcBucketMap.put(rpcBucket.bucketId, rpcBucket);
    }

    public boolean containsBucket(String bucketId){
        return RpcBucketMap.containsKey(bucketId);
    }

    public void removeBucket(String bucketId){
        if(RpcBucketMap.containsKey(bucketId))
            RpcBucketMap.remove(bucketId);
    }

    public void clearAll(){
        for(String key:RpcBucketMap.keySet()){
            RpcBucketMap.get(key).clearAll();
        }
        RpcBucketMap.clear();
    }

}
