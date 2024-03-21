package com.tiutiu;

import com.tiutiu.annotation.RpcReference;
import com.tiutiu.consumer.RpcInvokerProxy;
import com.tiutiu.consumer.RpcReferencePostProcessor;
import com.tiutiu.demo.service.TestService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan
@Import(RpcReferencePostProcessor.class)
public class Consumer {

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Consumer.class);
        Test test = context.getBean(Test.class);
        System.out.println(test.test(1, 2));
    }
}