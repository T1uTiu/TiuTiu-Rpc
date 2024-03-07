package com.tiutiu.router.impl;

import com.sun.source.tree.Tree;
import com.tiutiu.common.ServiceMeta;
import com.tiutiu.registry.RegistryFactory;
import com.tiutiu.registry.RegistryService;
import com.tiutiu.router.LoadBalancer;
import com.tiutiu.router.ServiceMetaRes;
import lombok.val;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer implements LoadBalancer {
    // 懒汉单例模式
    private static volatile ConsistentHashLoadBalancer instance;
    public static ConsistentHashLoadBalancer getInstance() {
        if (instance == null) {
            synchronized (ConsistentHashLoadBalancer.class) {
                if (instance == null) {
                    instance = new ConsistentHashLoadBalancer();
                }
            }
        }
        return instance;
    }
    // 虚拟节点个数
    private final static int VIRTUAL_NODE_SIZE = 10;
    @Override
    public ServiceMetaRes select(String serviceName, Object[] params) throws Exception {
        // 获得注册中心
        RegistryService registryService = RegistryFactory.get();
        List<ServiceMeta> serviceMetas = registryService.discoveries(serviceName);
        ServiceMeta curServiceMeta = getNode(
                addNodes(serviceMetas),
                params[0].hashCode()
        );
        return new ServiceMetaRes(
                curServiceMeta,
                serviceMetas
        );
    }
    public ServiceMeta getNode(TreeMap<Integer, ServiceMeta> ring, int hash){
        // 找到最近的一个节点
        Map.Entry<Integer, ServiceMeta> entry = ring.ceilingEntry(hash);
        // 如果没有则选择最小的节点
        if(entry == null){
            entry = ring.firstEntry();
        }
        return entry.getValue();

    }
    private TreeMap<Integer, ServiceMeta> addNodes(List<ServiceMeta> services){
        TreeMap<Integer, ServiceMeta> treeMap = new TreeMap<>();
        for(ServiceMeta service : services){
            for(int i = 0; i < VIRTUAL_NODE_SIZE; i++){
                String key = buildServiceKey(service) + "&&VN" + i;
                treeMap.put(key.hashCode(), service);
            }
        }
        return treeMap;
    }
    private String buildServiceKey(ServiceMeta serviceMeta){
        return serviceMeta.getServiceAddress() + ":" + serviceMeta.getServicePort();
    }
}
