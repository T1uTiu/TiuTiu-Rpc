package com.tiutiu;

import com.tiutiu.annotation.RpcReference;
import com.tiutiu.demo.service.TestService;
import org.springframework.stereotype.Component;

@Component
public class Test {
    @RpcReference
    TestService testService;

    public Integer test(Integer a, Integer b) {
        return testService.add(a, b);
    }
}
