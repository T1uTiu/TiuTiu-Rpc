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
import com.tiutiu.router.LoadBalancer;
import com.tiutiu.router.LoadBalancerFactory;
import com.tiutiu.router.ServiceMetaRes;
import com.tiutiu.tolerant.FaultTolerantFactory;
import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import lombok.val;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class RpcInvokerProxy implements InvocationHandler {
    private final String serviceVersion;
    private final long timeout;
    private final int retryCount;
    Logger logger = Logger.getLogger(RpcInvokerProxy.class.getName());
    RpcInvokerProxy(String serviceVersion, long timeout, int retryCount){
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.retryCount = retryCount;
    }
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
                .serviceVersion(serviceVersion)
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();
        request.setBody(rpcRequest);
        RpcClient rpcClient = new RpcClient();
        // 获得负载均衡策略
        LoadBalancer loadBalancer = LoadBalancerFactory.get();
        String serviceNameKey = RpcServiceNameBuilder.buildKey(rpcRequest.getServiceName(), rpcRequest.getServiceVersion());
        Object[] params = {request.getBody()};
        ServiceMetaRes serviceMetaRes = loadBalancer.select(serviceNameKey, params);
        var currentServiceMeta = serviceMetaRes.getCurServiceMeta();
        var otherServiceMetas = serviceMetaRes.getOtherServiceMetas();
        // 获得容错机制
        var faultTolerantStrategy = FaultTolerantFactory.get();
        // 发送请求
        int count = 0;
        RpcResponse response = null;
        while(count < retryCount){
            // 响应结构
            RpcFuture<RpcResponse> future = new RpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), timeout);
            RpcRequestHolder.REQUEST_MAP.put(requestId, future);
            // 发送请求
            try{
                rpcClient.sendRequest(currentServiceMeta, request);
                response = future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS);
                // 如果调用方有异常
                if(response.getException() != null){
                    throw response.getException();
                }
                logger.info("rpc调用成功");
                return response.getData();
            }catch (Throwable e){
                currentServiceMeta = faultTolerantStrategy.handle(otherServiceMetas);
                count++;
            }
        }

        throw new RuntimeException("rpc调用失败，达到最大调用次数："+retryCount);
    }
    public static Object getInstance(Class<?> clazz, String serviceVersion, long timeout, int retryCount){
        return Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                new RpcInvokerProxy(serviceVersion, timeout, retryCount)
        );
    }
}
