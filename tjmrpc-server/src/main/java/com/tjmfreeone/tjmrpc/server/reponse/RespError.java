package com.tjmfreeone.tjmrpc.server.reponse;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class RespError extends RespStatus {
    private String code = "1001";
    private String status = "error";
    private String reason = "unknown";

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String clientId;

    public RespError(String reason){
        this.reason = reason;
    }

    public RespError(String reason, String clientId){
        this.reason = reason;
        this.clientId = clientId;
    }
}
