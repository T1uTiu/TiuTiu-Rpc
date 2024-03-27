package com.tiutiu.tolerant.impl;

import com.tiutiu.common.ServiceMeta;
import com.tiutiu.tolerant.FaultTolerantStrategy;

import java.util.Collection;
import java.util.List;

public class FailFastStrategy implements FaultTolerantStrategy {
    @Override
    public ServiceMeta handle(Collection<ServiceMeta> otherServiceMetas) {
        throw new RuntimeException("rpc调用失败，触发快速失败机制");
    }
}
