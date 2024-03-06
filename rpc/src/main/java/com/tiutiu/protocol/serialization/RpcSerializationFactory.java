package com.tiutiu.protocol.serialization;

import com.tiutiu.protocol.serialization.impl.HessianSerialization;

public class RpcSerializationFactory {
    public static RpcSerialization get(){
        return new HessianSerialization();
    }
}
