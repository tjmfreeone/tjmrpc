package com.tjmfreeone.tjmrpc.client.common;


public abstract class Function {

    String functionId = null;

    ReqMethod REQ_METHOD = null;

     public abstract String getFunctionId();

     public abstract ReqMethod getReqMethod();
}
