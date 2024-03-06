package com.tiutiu.consumer;

import com.tiutiu.common.RpcRequest;
import com.tiutiu.common.RpcResponse;
import com.tiutiu.common.RpcServiceNameBuilder;
import com.tiutiu.common.ServiceMeta;
import com.tiutiu.protocol.MsgHeader;
import com.tiutiu.protocol.RpcProtocol;
import com.tiutiu.provider.RpcServiceHolder;
import com.tiutiu.registry.RegistryFactory;
import com.tiutiu.registry.RegistryService;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class RpcInvokerProxy implements InvocationHandler {
    Logger logger = Logger.getLogger(RpcInvokerProxy.class.getName());
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcProtocol<RpcRequest> request = new RpcProtocol<>();
        // 构造信息头
        long requestId = RpcRequestHolder.REQUEST_ID_GEN.incrementAndGet();
        MsgHeader msgHeader = MsgHeader.builder()
                .magic(RpcProtocol.MAGIC)
                .version(RpcProtocol.VERSION)
                .msgType(RpcProtocol.MSG_TYPE_REQUEST)
                .status((byte) 0)
                .requestId(requestId)
                .build();
        request.setMsgHeader(msgHeader);
        // 构建请求体
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceVersion("1.0")
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();
        request.setBody(rpcRequest);
        RpcClient rpcClient = new RpcClient();
        // 注册中心
        RegistryService registryService = RegistryFactory.get();
        List<ServiceMeta> serviceMetas = registryService.discoveries(
                RpcServiceNameBuilder.buildKey(rpcRequest.getServiceName(), rpcRequest.getServiceVersion())
        );
        // 发送消息
        rpcClient.sendRequest(serviceMetas.get(0), request);
        // 等待响应
        RpcFuture<RpcResponse> future = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), 3000);
        RpcRequestHolder.REQUEST_MAP.put(requestId, future);
        RpcResponse response = future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS);
        logger.info("rpc调用成功");
        return response.getData();
    }
    public static Object getInstance(Class<?> clazz){
        return Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                new RpcInvokerProxy()
        );
    }
}
