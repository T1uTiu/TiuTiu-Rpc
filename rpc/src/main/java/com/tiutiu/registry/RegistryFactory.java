package com.tiutiu.registry;

import com.tiutiu.registry.impl.*;

public class RegistryFactory {
    public static RegistryService get() throws Exception {
        return ZookeeperRegistry.getZookeeperRegistry();
    }
}
