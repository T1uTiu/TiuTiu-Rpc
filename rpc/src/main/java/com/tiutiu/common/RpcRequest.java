package com.tiutiu.common;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class RpcRequest implements Serializable {
    private String serviceVersion;
    private String serviceName;
    private String methodName;

    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
