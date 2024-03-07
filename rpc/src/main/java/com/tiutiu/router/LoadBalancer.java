package com.tiutiu.router;

public interface LoadBalancer {
    ServiceMetaRes select(String serviceName) throws Exception;
}
