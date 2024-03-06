package com.tiutiu.registry;

import com.tiutiu.common.ServiceMeta;

import java.util.List;

public interface RegistryService {

    /**
     * 服务注册
     */
    void register(ServiceMeta serviceMeta) throws Exception;
    /**
     * 获取 serviceName 下的所有服务
     */
    List<ServiceMeta> discoveries(String serviceName);
//    /**
//     * 关闭
//     * @throws IOException
//     */
//    void destroy() throws IOException;

}
