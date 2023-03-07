package com.tjmfreeone.tjmrpc.client.common;


import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class DeferredTaskManager {
    private static volatile DeferredTaskManager INSTANCE;

    private final ConcurrentHashMap<String, DeferredTask> map;
    private Timer timer = new Timer();
    long timeout = 5000;


    private DeferredTaskManager(){
        if(INSTANCE!=null){
            throw new RuntimeException("单例已被创建"); // 防止反射破坏
        }
        map = new ConcurrentHashMap<>();
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
                if(map.containsKey(deferredTask.getId())){
                    deferredTask.getDeferredObject().reject("client inner timeout");
                    get().removeAnyway(deferredTask.getId());
                }
            }
        }, timeout);
    }

    public DeferredTask getTask(String id){
        return map.getOrDefault(id, null);
    }

    public void removeAnyway(String id){
        if(map.containsKey(id)){
            map.remove(id);
        }
    }
}
