package com.tiutiu.consumer;

import io.netty.util.concurrent.Promise;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RpcFuture<T> {
    private Promise<T> promise;
    private long timeout;
}
