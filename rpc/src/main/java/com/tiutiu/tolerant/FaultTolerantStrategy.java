package com.tiutiu.tolerant;

import com.tiutiu.common.ServiceMeta;

import java.util.Collection;
import java.util.List;

public interface FaultTolerantStrategy {
    ServiceMeta handle(Collection<ServiceMeta> otherServiceMetas) throws Throwable;
}
