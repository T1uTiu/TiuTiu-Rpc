package com.tiutiu.registry;

public interface RegistryService {

    /**
     * 服务注册
     */
    void register(String className, Class<?> implClass) throws Exception;
//
//    /**
//     * 服务注销
//     * @param serviceMeta
//     * @throws Exception
//     */
//    void unRegister(ServiceMeta serviceMeta) throws Exception;
//
//
    /**
     * 获取 serviceName 下的所有服务
     */
    Class<?> discoveries(String className);
//    /**
//     * 关闭
//     * @throws IOException
//     */
//    void destroy() throws IOException;

}
