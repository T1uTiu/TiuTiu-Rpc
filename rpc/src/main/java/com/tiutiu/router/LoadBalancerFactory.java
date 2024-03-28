package com.tiutiu.router;

import com.tiutiu.spi.ExtensionLoader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoadBalancerFactory {
    private static Map<String, Class<?>> classMap;
    private static Map<String, Object> singletonMap = new ConcurrentHashMap<>();
    private static boolean isInit = false;
    public static LoadBalancer get(String loadBalanceRule){
        if(!isInit){
            init();
        }
        if(!singletonMap.containsKey(loadBalanceRule)){
            try {
                singletonMap.put(loadBalanceRule, classMap.get(loadBalanceRule).getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
        return (LoadBalancer) singletonMap.get(loadBalanceRule);
    }
    public static void init(){
        try {
            classMap = ExtensionLoader.getInstance().loadExtension(LoadBalancer.class);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        isInit = true;
    }
}
