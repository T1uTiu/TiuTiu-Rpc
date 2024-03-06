package com.tiutiu.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceMeta implements Serializable {
    String serviceName;
    String serviceVersion;
    String serviceAddress;
    int servicePort;
}
