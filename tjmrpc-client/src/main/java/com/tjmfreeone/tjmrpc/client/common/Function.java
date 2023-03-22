package com.tjmfreeone.tjmrpc.client.common;


public interface Function {

    String functionId = null;

    ReqMethod REQ_METHOD = null;

     String getFunctionId();

     ReqMethod getReqMethod();
}
