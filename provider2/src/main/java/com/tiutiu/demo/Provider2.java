package com.tiutiu.demo;

import com.tiutiu.common.RpcServiceNameBuilder;
import com.tiutiu.common.ServiceMeta;
import com.tiutiu.demo.service.TestService;
import com.tiutiu.demo.service.impl.TestServiceImpl2;
import com.tiutiu.provider.RpcServer;
import com.tiutiu.provider.RpcServiceHolder;
import com.tiutiu.registry.RegistryFactory;
import com.tiutiu.registry.RegistryService;

public class Provider2 {
    public static void main(String[] args) throws Exception {
        // 注册中心
        RegistryService registryService = RegistryFactory.get();
        // 服务元信息
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .serviceName(TestService.class.getName())
                .serviceVersion("1.0")
                .serviceAddress("localhost")
                .servicePort(8090)
                .build();
        // 注册服务
        registryService.register(serviceMeta);
        // 缓存服务实现类
        RpcServiceHolder.serviceMap.put(
                RpcServiceNameBuilder.buildKey(TestService.class.getName(), "1.0"),
                TestServiceImpl2.class);

        RpcServer rpcServer = new RpcServer();
        rpcServer.start("localhost", 8090);
    }
}