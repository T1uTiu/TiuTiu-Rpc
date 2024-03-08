package com.tiutiu.protocol.serialization;

import com.tiutiu.protocol.serialization.impl.HessianSerialization;
import com.tiutiu.protocol.serialization.impl.KryoSerialization;

public class RpcSerializationFactory {
    public static RpcSerialization get(){
        return HessianSerialization.getInstance();
//        return KryoSerialization.getInstance();
    }
}
