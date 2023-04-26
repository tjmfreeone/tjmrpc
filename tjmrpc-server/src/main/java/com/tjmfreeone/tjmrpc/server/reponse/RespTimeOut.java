package com.tjmfreeone.tjmrpc.server.reponse;

import lombok.Data;

@Data
public class RespTimeOut extends RespStatus {
    private String code = "1003";
    private String status = "time out";
}
