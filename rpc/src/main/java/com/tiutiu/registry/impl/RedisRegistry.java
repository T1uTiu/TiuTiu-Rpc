package com.tiutiu.registry.impl;

import com.tiutiu.registry.RegistryService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisRegistry implements RegistryService {
    private static RedisRegistry redisRegistry;
    public static RedisRegistry getRedisRegistry() {
        // 单例模式
        if (redisRegistry == null) {
            synchronized (RedisRegistry.class) {
                if (redisRegistry == null) {
                    redisRegistry = new RedisRegistry();
                }
            }
        }
        return redisRegistry;
    }
    private RedisRegistry(){
        serviceMap = new ConcurrentHashMap<>();
    }
    Map<String, Class<?>> serviceMap;
    public void register(String className, Class<?> implClass) {
        serviceMap.put(className, implClass);
    }
    public Class<?> discoveries(String className) {
        return serviceMap.get(className);
    }
}
