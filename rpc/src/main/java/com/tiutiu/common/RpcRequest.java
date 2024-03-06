package com.tiutiu.common;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@Builder
public class RpcRequest implements Serializable {
    private String serviceVersion;
    private String className;
    private String methodName;

    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
