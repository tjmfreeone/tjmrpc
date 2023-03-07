package com.tjmfreeone.tjmrpc.server.reponse;

import lombok.Data;

@Data
public class RespTimeOut implements RespStatus {
    private String code = "1003";
    private String status = "time out";
}
