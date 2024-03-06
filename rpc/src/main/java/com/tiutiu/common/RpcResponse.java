package com.tiutiu.common;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class RpcResponse implements Serializable {
    private Object data;
    private Class dataClass;
    private String message;
    private Exception exception;
}
