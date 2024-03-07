package com.tiutiu.demo.service.impl;

import com.tiutiu.demo.service.TestService;

public class TestServiceImpl2 implements TestService {
    @Override
    public Integer add(Integer a, Integer b) {
        return a*b;
    }
}
