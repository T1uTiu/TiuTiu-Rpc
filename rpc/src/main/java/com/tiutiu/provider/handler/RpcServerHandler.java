package com.tiutiu.provider.handler;

import com.tiutiu.common.RpcRequest;
import com.tiutiu.common.RpcResponse;
import com.tiutiu.protocol.MsgHeader;
import com.tiutiu.protocol.RpcProtocol;
import com.tiutiu.registry.RegistryFactory;
import com.tiutiu.registry.RegistryService;
import io.netty.buffer.ByteBuf;
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
        // 注册中心
        RegistryService registryService = RegistryFactory.get();
        // 获取服务实现类
        String className = request.getClassName();
        String methodName = request.getMethodName();
        Class<?> service = registryService.discoveries(className);
        Object serviceBean = null;
        Method method = null;
        // 反射构造实现类
        try {
            serviceBean = service.newInstance();
            // 反射调用方法
            method = service.getMethod(methodName, request.getParameterTypes());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
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
            Object res = method.invoke(serviceBean, request.getParameters());
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
