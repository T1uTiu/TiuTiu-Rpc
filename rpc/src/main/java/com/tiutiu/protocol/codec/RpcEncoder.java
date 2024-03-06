package com.tiutiu.protocol.codec;

import com.tiutiu.protocol.MsgHeader;
import com.tiutiu.protocol.RpcProtocol;
import com.tiutiu.protocol.serialization.RpcSerialization;
import com.tiutiu.protocol.serialization.RpcSerializationFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcProtocol<Object> msg, ByteBuf byteBuf) throws Exception {
        // 获取消息头
        MsgHeader header = msg.getMsgHeader();
        // 写入魔数
        byteBuf.writeShort(header.getMagic());
        // 写入版本号
        byteBuf.writeByte(header.getVersion());
        // 写入消息类型
        byteBuf.writeByte(header.getMsgType());
        // 写入状态
        byteBuf.writeByte(header.getStatus());
        // 写入请求ID
        byteBuf.writeLong(header.getRequestId());
        // 序列化
        RpcSerialization serialization = RpcSerializationFactory.get();
        byte[] data = serialization.serialize(msg.getBody());
        // 写入数据长度
        byteBuf.writeInt(data.length);
        // 写入数据
        byteBuf.writeBytes(data);
    }
}
