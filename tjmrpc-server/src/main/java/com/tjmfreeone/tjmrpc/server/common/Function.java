package com.tjmfreeone.tjmrpc.server.common;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Function{

    @EqualsAndHashCode.Include()
    private String functionId;

    @EqualsAndHashCode.Include()
    private ReqMethod reqMethod;
}
