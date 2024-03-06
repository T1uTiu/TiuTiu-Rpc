package com.tiutiu.protocol.codec;

import com.tiutiu.common.RpcRequest;
import com.tiutiu.common.RpcResponse;
import com.tiutiu.protocol.MsgHeader;
import com.tiutiu.protocol.RpcProtocol;
import com.tiutiu.protocol.serialization.RpcSerialization;
import com.tiutiu.protocol.serialization.RpcSerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class RpcDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        // 还没有接受完消息头
        if(in.readableBytes() < RpcProtocol.HEADER_TOTAL_LEN){
            return;
        }
        in.markReaderIndex();
        // 读取魔数
        short magic = in.readShort();
        if(magic != RpcProtocol.MAGIC){
            throw new IllegalArgumentException("Magic number is illegal");
        }
        byte version = in.readByte();
        byte msgType = in.readByte();
        byte status = in.readByte();
        long requestId = in.readLong();
        // 消息体长度
        int dataLength = in.readInt();
        // 如果可读字节数小于消息体长度，说明还没有接收完整个消息体，回退并返回
        if (in.readableBytes() < dataLength) {
            // 回退标记位置
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[dataLength];
        in.readBytes(data);

        // 构建消息头
        MsgHeader header = MsgHeader.builder()
                .magic(magic)
                .version(version)
                .msgType(msgType)
                .status(status)
                .requestId(requestId)
                .msgLen(dataLength)
                .build();
        // 序列化器
        RpcSerialization serialization = RpcSerializationFactory.get();
        switch (msgType) {
            case RpcProtocol.MSG_TYPE_REQUEST -> {
                RpcRequest request = serialization.deserialize(data, RpcRequest.class);
                if(request != null){
                    RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
                    protocol.setMsgHeader(header);
                    protocol.setBody(request);
                    out.add(protocol);
                }
            }
            case RpcProtocol.MSG_TYPE_RESPONSE -> {
                RpcResponse response = serialization.deserialize(data, RpcResponse.class);
                if(response != null){
                    RpcProtocol<RpcResponse> protocol = new RpcProtocol<>();
                    protocol.setMsgHeader(header);
                    protocol.setBody(response);
                    out.add(protocol);
                }
            }
        }
    }
}
