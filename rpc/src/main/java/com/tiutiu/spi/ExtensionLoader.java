package com.tiutiu.spi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class ExtensionLoader {
    // 单例模式
    private volatile static ExtensionLoader instance;
    public static ExtensionLoader getInstance(){
        if(instance == null){
            synchronized (ExtensionLoader.class){
                if(instance == null){
                    instance = new ExtensionLoader();
                }
            }
        }
        return instance;
    }
    public static String[] EXTENSION_DIRECTORY = {
            "META-INF/sys-trpc",
            "META-INF/tiutiu-rpc"
    };
    /**
     * 通过SPI机制根据接口加载实现类
     * @param clazz 接口类型
     */
    public Map<String, Class<?>> loadExtension(Class<?> clazz) throws IOException, ClassNotFoundException {
        if(clazz == null){
            throw new IllegalArgumentException("Extension type is null");
        }
        ClassLoader classLoader = getClass().getClassLoader();
        Map<String, Class<?>> classMap = new HashMap<>();
        for(String path: EXTENSION_DIRECTORY){
            String fileName = path + "/"+clazz.getName();
            Enumeration<URL> urls = classLoader.getResources(fileName);
            while (urls.hasMoreElements()){
                URL url = urls.nextElement();
                // 读取文件内容
                InputStreamReader reader = new InputStreamReader(url.openStream());
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line;
                while((line = bufferedReader.readLine()) != null){
                    String[] split = line.split("=");
                    String key = split[0];
                    String className = split[1];
                    Class<?> clz = Class.forName(className);
                    classMap.put(key, clz);
                }

            }
        }
        return classMap;
    }
}
