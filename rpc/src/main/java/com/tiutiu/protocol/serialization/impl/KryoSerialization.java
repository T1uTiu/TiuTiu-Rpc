package com.tiutiu.protocol.serialization.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.tiutiu.common.RpcRequest;
import com.tiutiu.common.RpcResponse;
import com.tiutiu.protocol.serialization.RpcSerialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerialization implements RpcSerialization {
    // 懒汉单例模式
    private volatile static KryoSerialization instance;
    public static KryoSerialization getInstance(){
        if(instance == null){
            synchronized (KryoSerialization.class){
                if(instance == null){
                    instance = new KryoSerialization();
                }
            }
        }
        return instance;
    }
    // Kryo不是线程安全的，所以使用ThreadLocal来保证线程安全
    private final ThreadLocal<Kryo> kryoThreadLocal;
    private KryoSerialization(){
        kryoThreadLocal = ThreadLocal.withInitial(() -> {
            Kryo kryo = new Kryo();
            kryo.register(RpcRequest.class);
            kryo.register(RpcResponse.class);
            return kryo;
        });
    }
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Output output = new Output(byteArrayOutputStream);
        Kryo kryo = kryoThreadLocal.get();
        kryo.writeObject(output, obj);
        kryoThreadLocal.remove();
        return output.toBytes();
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        Input input = new Input(byteArrayInputStream);
        Kryo kryo = kryoThreadLocal.get();
        Object res = kryo.readObject(input, clz);
        kryoThreadLocal.remove();
        return clz.cast(res);
    }
}
