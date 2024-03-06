package com.tiutiu.provider.handler;

import com.tiutiu.common.RpcRequest;
import com.tiutiu.common.RpcResponse;
import com.tiutiu.common.RpcServiceNameBuilder;
import com.tiutiu.protocol.MsgHeader;
import com.tiutiu.protocol.RpcProtocol;
import com.tiutiu.provider.RpcServiceHolder;
import com.tiutiu.registry.RegistryFactory;
import com.tiutiu.registry.RegistryService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

public class RpcServerHandler extends SimpleChannelInboundHandler {
    Logger logger = Logger.getLogger(RpcServerHandler.class.getName());
    @SuppressWarnings("unchecked")
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcProtocol<RpcRequest> request = (RpcProtocol<RpcRequest>) msg;
        RpcProtocol<RpcResponse> response = handleRequest(request);
        ctx.writeAndFlush(response);

    }
    private RpcProtocol<RpcResponse> handleRequest(RpcProtocol<RpcRequest> msg){
        RpcRequest request = msg.getBody();
        // 获取服务实现类
        String serviceName = request.getServiceName();
        String serviceVersion = request.getServiceVersion();
        Class<?> serviceClass = RpcServiceHolder.serviceMap.get(
                RpcServiceNameBuilder.buildKey(serviceName, serviceVersion));
        String methodName = request.getMethodName();

        Object service = null;
        Method method = null;
        // 反射构造实现类
        try {
            service = serviceClass.getDeclaredConstructor().newInstance();
            method = serviceClass.getMethod(methodName, request.getParameterTypes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RpcProtocol<RpcResponse> response = new RpcProtocol<>();
        // 响应头
        MsgHeader msgHeader = MsgHeader.builder()
                .magic(RpcProtocol.MAGIC)
                .version(RpcProtocol.VERSION)
                .msgType(RpcProtocol.MSG_TYPE_RESPONSE)
                .status((byte) 0)
                .requestId(msg.getMsgHeader().getRequestId())
                .build();
        // 响应体
        RpcResponse rpcResponse = RpcResponse.builder().build();
        try{
            Object res = method.invoke(service, request.getParameters());
            rpcResponse.setData(res);
            rpcResponse.setDataClass(res.getClass());
            rpcResponse.setMessage("方法调用成功");
        }catch (IllegalAccessException | InvocationTargetException e){
            rpcResponse.setException(e);
            rpcResponse.setMessage("方法调用失败");
        }
        response.setMsgHeader(msgHeader);
        response.setBody(rpcResponse);
        return response;
    }
}
