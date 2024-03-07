package com.tiutiu.router.impl;

import com.tiutiu.common.ServiceMeta;
import com.tiutiu.registry.RegistryFactory;
import com.tiutiu.registry.RegistryService;
import com.tiutiu.router.LoadBalancer;
import com.tiutiu.router.ServiceMetaRes;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalancer implements LoadBalancer {
    // 懒汉单例模式
    private static volatile RoundRobinLoadBalancer instance;
    public static RoundRobinLoadBalancer getInstance() {
        if (instance == null) {
            synchronized (RoundRobinLoadBalancer.class) {
                if (instance == null) {
                    instance = new RoundRobinLoadBalancer();
                }
            }
        }
        return instance;
    }
    private final AtomicInteger roundRobin = new AtomicInteger(0);
    @Override
    public ServiceMetaRes select(String serviceName, Object[] params) throws Exception {
        // 获得注册中心
        RegistryService registryService = RegistryFactory.get();
        List<ServiceMeta> serviceMetas = registryService.discoveries(serviceName);
        // 轮循
        roundRobin.addAndGet(1);
        // 判断达到最大值则设为0
        if(roundRobin.get() == Integer.MAX_VALUE){
            roundRobin.set(0);
        }
        // 轮询ID取余size
        int index = roundRobin.get() % serviceMetas.size();
        return new ServiceMetaRes(
                serviceMetas.get(index),
                serviceMetas
        );
    }
}
