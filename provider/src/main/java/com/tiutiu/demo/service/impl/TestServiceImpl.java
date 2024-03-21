package com.tiutiu.demo.service.impl;

import com.tiutiu.annotation.RpcService;
import com.tiutiu.demo.service.TestService;

@RpcService
public class TestServiceImpl implements TestService {
    @Override
    public Integer add(Integer a, Integer b) {
        return a+b;
    }
}
