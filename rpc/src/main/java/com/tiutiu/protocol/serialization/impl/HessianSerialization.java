package com.tiutiu.protocol.serialization.impl;

import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;
import com.tiutiu.protocol.serialization.RpcSerialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerialization implements RpcSerialization {
    @Override
    public <T> byte[] serialize(T obj) throws IOException{
        if(obj == null){
            throw new NullPointerException();
        }
        byte[] res;
        HessianSerializerOutput hessianOutput;
        try(ByteArrayOutputStream os = new ByteArrayOutputStream()){
            hessianOutput = new HessianSerializerOutput((os));
            hessianOutput.writeObject(obj);
            hessianOutput.flush();
            res = os.toByteArray();
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        if(data == null){
            throw new NullPointerException();
        }
        T res;
        try(ByteArrayInputStream is = new ByteArrayInputStream(data)){
            HessianSerializerInput hessianInput = new HessianSerializerInput(is);
            res = (T) hessianInput.readObject(clz);
        }
        return res;
    }
}
