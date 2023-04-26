package com.tjmfreeone.tjmrpc.server.reponse;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class RespSuccess extends RespStatus {
    private String code = "0000";
    private String status = "success";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String clientId;

    private Object result;

    public RespSuccess(Object result){
        this.result = result;
    }

    public RespSuccess(Object result, String clientId){
        this.result = result;
        this.clientId = clientId;
    }
}
