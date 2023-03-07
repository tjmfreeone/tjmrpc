package com.tjmfreeone.tjmrpc.server.common;

import lombok.*;

@Data
@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Function {

    @EqualsAndHashCode.Include()
    private String functionId;
}
