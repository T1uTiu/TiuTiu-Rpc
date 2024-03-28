package com.tiutiu.router;

public interface LoadBalancer {
    static LoadBalancer getInstance() {
        return null;
    }

    ServiceMetaRes select(String serviceName, Object[] params) throws Exception;
}
