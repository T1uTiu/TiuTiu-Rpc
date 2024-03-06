package com.tiutiu;

import com.tiutiu.demo.service.TestService;
import com.tiutiu.demo.service.impl.TestServiceImpl;
import com.tiutiu.provider.RpcServer;
import com.tiutiu.registry.RegistryFactory;
import com.tiutiu.registry.RegistryService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Provider {
    public static void main(String[] args) throws Exception {
        RegistryService registryService = RegistryFactory.get();
        registryService.register(TestService.class.getName(), TestServiceImpl.class);

        RpcServer rpcServer = new RpcServer();
        rpcServer.start("localhost", 8088);
    }
}