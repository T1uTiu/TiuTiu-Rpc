package com.tiutiu.router;

import com.tiutiu.router.impl.RoundRobinLoadBalancer;

public class LoadBalancerFactory {
    public static LoadBalancer get() {
        return RoundRobinLoadBalancer.getInstance();
    }
}
