package com.tiutiu.tolerant;

import com.tiutiu.tolerant.impl.FailOverStrategy;

public class FaultTolerantFactory {
    public static FaultTolerantStrategy get(){
        return FailOverStrategy.getInstance();
    }
}
