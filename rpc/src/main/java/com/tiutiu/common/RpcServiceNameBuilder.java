package com.tiutiu.common;

public class RpcServiceNameBuilder {
    public static String buildKey(String serviceName, String serviceVersion) {
        return serviceName + "#" + serviceVersion;
    }
}
