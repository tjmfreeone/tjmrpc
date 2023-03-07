//
// rpcClient是对每个netty的channel进行的封装, 并添加了一些属性
//

package com.tjmfreeone.tjmrpc.server;

import io.netty.channel.Channel;
import lombok.*;


@Data
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcClient {
    private String bucketId;
    private String clientId;
    private Channel channel;

    public static class RpcClientBuilder{
        public RpcClientBuilder(){}
    }
}
