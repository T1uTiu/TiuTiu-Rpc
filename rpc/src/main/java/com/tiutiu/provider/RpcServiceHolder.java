package com.tiutiu.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RpcServiceHolder {
    public static final Map<String, Class<?>> serviceMap = new ConcurrentHashMap<>();
}
