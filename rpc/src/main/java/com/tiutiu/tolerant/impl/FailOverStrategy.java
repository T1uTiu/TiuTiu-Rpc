package com.tiutiu.tolerant.impl;

import com.tiutiu.common.ServiceMeta;
import com.tiutiu.tolerant.FaultTolerantStrategy;

import java.util.Collection;
import java.util.List;

public class FailOverStrategy implements FaultTolerantStrategy {
    private enum Singleton{
        INSTANCE;
        private final FailOverStrategy instance = new FailOverStrategy();
    }
    public static FailOverStrategy getInstance(){
        return Singleton.INSTANCE.instance;
    }
    @Override
    public ServiceMeta handle(Collection<ServiceMeta> otherServiceMetas) {
        ServiceMeta currentServiceMeta;
        if(!otherServiceMetas.isEmpty()){
            ServiceMeta next = otherServiceMetas.iterator().next();
            currentServiceMeta = next;
            otherServiceMetas.remove(next);
            return currentServiceMeta;
        }else{
            // 无其他服务可转移
            throw new RuntimeException("rpc调用失败，无服务可用");
        }
    }
}
