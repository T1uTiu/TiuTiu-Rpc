package com.tiutiu;

import com.tiutiu.consumer.RpcInvokerProxy;
import com.tiutiu.demo.service.TestService;

public class Consumer {
    public static void main(String[] args) {
        TestService testService = (TestService) RpcInvokerProxy.getInstance(TestService.class);
        Integer ans = testService.add(1,2);
        System.out.println(ans);
    }
}