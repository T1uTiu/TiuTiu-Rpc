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
    List<ServiceMeta> otherServiceMetas;
    public ServiceMetaRes(ServiceMeta curServiceMeta, List<ServiceMeta> otherServiceMetas){
        this.curServiceMeta = curServiceMeta;
        // 如果只有一个服务
        if(otherServiceMetas.size() == 1){
            this.otherServiceMetas = new ArrayList<>();
        }else{
            this.otherServiceMetas = new ArrayList<>(otherServiceMetas);
            this.otherServiceMetas.remove(curServiceMeta);
        }

    }
}
