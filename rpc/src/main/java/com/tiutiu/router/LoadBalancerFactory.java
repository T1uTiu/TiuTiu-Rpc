package com.tiutiu.router;

import com.tiutiu.router.impl.*;

public class LoadBalancerFactory {
    public static LoadBalancer get() {
        return ConsistentHashLoadBalancer.getInstance();
    }
}
