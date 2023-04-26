/*
* 支持在客户端构建异步任务，触发请求跟接收结果凭taskId关联
* */
package com.tjmfreeone.tjmrpc.client.common;


import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DeferredTaskManager {
    private static volatile DeferredTaskManager INSTANCE;

    private final ConcurrentHashMap<String, DeferredTask> map;
    private Timer timer;
    long timeout = 5000;


    private DeferredTaskManager(){
        if(INSTANCE!=null){
            throw new RuntimeException("单例已被创建"); // 防止反射破坏
        }
        map = new ConcurrentHashMap<>();
        timer = new Timer();
    }

    public static DeferredTaskManager get(){
        if(INSTANCE==null){
            synchronized (DeferredTaskManager.class){
                if(INSTANCE==null){
                    INSTANCE = new DeferredTaskManager();
                }
            }
        }
        return INSTANCE;
    }

    public void addTask(DeferredTask deferredTask){
        map.put(deferredTask.getId(), deferredTask);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(map.containsKey(deferredTask.getId())&&!deferredTask.isFinished()){
                    deferredTask.getDeferredObject().reject("client inner timeout");
                    get().removeAnyway(deferredTask.getId());
                }
            }
        }, timeout);
    }

    public DeferredTask getTask(String id){
        return map.getOrDefault(id, null);
    }

    public long sizeOfTasks(){
        return map.size();
    }

    public void removeAnyway(String id){
        map.remove(id);
    }
}
