package com.tjmfreeone.tjmrpc.server.Listener;

import com.tjmfreeone.tjmrpc.server.netty.NettyServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component
public class NettyStartListener implements ApplicationRunner {
    @Resource
    private NettyServer nettyServer;

    @Value("${ws-port}")
    Integer port;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.nettyServer.start(port);
    }

}
