package com.tiutiu.registry.impl;

import com.tiutiu.common.RpcServiceNameBuilder;
import com.tiutiu.common.ServiceMeta;
import com.tiutiu.provider.RpcServiceHolder;
import com.tiutiu.registry.RegistryService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ZookeeperRegistry implements RegistryService {
    private static volatile ZookeeperRegistry zookeeperRegistry;
    // 单例模式
    public static ZookeeperRegistry getZookeeperRegistry() throws Exception {
        if (zookeeperRegistry == null) {
            synchronized (ZookeeperRegistry.class) {
                if (zookeeperRegistry == null) {
                    zookeeperRegistry = new ZookeeperRegistry();
                }
            }
        }
        return zookeeperRegistry;
    }
    // 连接失败等待重试时间
    public static final int BASE_SLEEP_TIME_MS = 1000;
    // 重试次数
    public static final int MAX_RETRIES = 3;
    // 根路径
    public static final String ZK_BASE_PATH = "/tiutiu_rpc";
    private final ServiceDiscovery<ServiceMeta> serviceDiscovery;
    private ZookeeperRegistry() throws Exception{
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                "localhost:2181",
                new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES)
        );
        client.start();
        JsonInstanceSerializer<ServiceMeta> serializer = new JsonInstanceSerializer<>(ServiceMeta.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_BASE_PATH)
                .build();
        this.serviceDiscovery.start();
    }

    /**
     * 服务注册
     * @param serviceMeta
     * @throws Exception
     */
    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance.<ServiceMeta>builder()
                .name(RpcServiceNameBuilder.buildKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion()))
                .address(serviceMeta.getServiceAddress())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.registerService(serviceInstance);
    }

    @Override
    public List<ServiceMeta> discoveries(String serviceName) {
        try{
            Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
            return serviceInstances.stream()
                    .map(ServiceInstance::getPayload).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }
}
