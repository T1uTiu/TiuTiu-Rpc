package com.tiutiu.demo.service.impl;

import com.tiutiu.demo.service.TestService;
import org.springframework.stereotype.Component;


public class TestServiceImpl2 implements TestService {
    @Override
    public Integer add(Integer a, Integer b) {
        return a*b;
    }
}
