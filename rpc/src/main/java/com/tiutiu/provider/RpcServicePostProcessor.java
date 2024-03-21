package com.tiutiu.provider;

import com.tiutiu.annotation.RpcService;
import com.tiutiu.common.RpcServiceNameBuilder;
import com.tiutiu.common.ServiceMeta;
import com.tiutiu.registry.RegistryFactory;
import com.tiutiu.registry.RegistryService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class RpcServicePostProcessor implements BeanPostProcessor {
    /**
     * 服务注册
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();
        RpcService rpcService = beanClass.getAnnotation(RpcService.class);
        if(rpcService != null){
            // 默认选择第一个实现
            String serviceName = beanClass.getInterfaces()[0].getName();
            if(!rpcService.serviceImpl().equals(void.class)){
                serviceName = rpcService.serviceImpl().getName();
            }
            String serviceVersion = rpcService.serviceVersion();
            // 注册
            try {
                RegistryService registryService = RegistryFactory.get();
                ServiceMeta serviceMeta = ServiceMeta.builder()
                        .serviceName(serviceName)
                        .serviceVersion(serviceVersion)
                        .serviceAddress("localhost")
                        .servicePort(8088)
                        .build();
                registryService.register(serviceMeta);
                RpcServiceHolder.serviceMap.put(
                        RpcServiceNameBuilder.buildKey(serviceName, serviceVersion),
                        beanClass
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bean;
    }

}
