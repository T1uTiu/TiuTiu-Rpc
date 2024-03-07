package com.tiutiu.router;

import com.tiutiu.common.ServiceMeta;
import lombok.Data;
import lombok.Getter;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class ServiceMetaRes {
    ServiceMeta curServiceMeta;
    Collection<ServiceMeta> otherServiceMetas;
    public ServiceMetaRes(ServiceMeta curServiceMeta, Collection<ServiceMeta> otherServiceMetas){
        this.curServiceMeta = curServiceMeta;
        // 如果只有一个服务
        if(otherServiceMetas.size() == 1){
            otherServiceMetas = new ArrayList<>();
        }else{
            otherServiceMetas.remove(curServiceMeta);
        }
        this.otherServiceMetas = otherServiceMetas;

    }
}
