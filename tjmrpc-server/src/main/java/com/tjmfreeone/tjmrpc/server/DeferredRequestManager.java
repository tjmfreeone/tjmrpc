package com.tjmfreeone.tjmrpc.server;

import com.tjmfreeone.tjmrpc.server.reponse.RespStatus;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ConcurrentHashMap;

public class DeferredRequestManager {
    private static volatile DeferredRequestManager INSTANCE;

    private static volatile ConcurrentHashMap<String, DeferredResult<RespStatus>> map;

    private DeferredRequestManager(){
        if(INSTANCE!=null){
            throw new RuntimeException("单例已被创建"); // 防止反射破坏
        }

    }

    public static DeferredRequestManager get(){
        if(INSTANCE==null){
            synchronized (DeferredRequestManager.class){
                if(INSTANCE==null){
                    INSTANCE = new DeferredRequestManager();
                    map = new ConcurrentHashMap<>();
                }
            }
        }
        return INSTANCE;
    }

    public void removeAnyway(String requestId){
        map.remove(requestId);
    }

    public boolean hasRequestId(String requestId){
        return map.containsKey(requestId);
    }

    public void add(String requestId, DeferredResult<RespStatus> deferredResult){
        map.put(requestId, deferredResult);
    }

    public DeferredResult<RespStatus> pop(String requestId){
        DeferredResult<RespStatus> deferredResult = map.getOrDefault(requestId, null);
        removeAnyway(requestId);
        return deferredResult;
    }

}
