package com.tiutiu.consumer;

import com.tiutiu.annotation.RpcReference;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

public class RpcReferencePostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // 获得字段
        Field[] fields = bean.getClass().getDeclaredFields();
        for(Field field : fields){
            if(field.isAnnotationPresent(RpcReference.class)){
                RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                Class<?> type = field.getType();
                field.setAccessible(true);
                Object proxy = RpcInvokerProxy.getInstance(type, rpcReference.version(), rpcReference.timeout(), rpcReference.retryCount(), rpcReference.loadBalanceRule());
                // 将代理对象设置给字段
                try{
                    field.set(bean, proxy);
                    field.setAccessible(false);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
