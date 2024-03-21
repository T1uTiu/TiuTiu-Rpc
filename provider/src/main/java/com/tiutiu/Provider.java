package com.tiutiu;

import com.tiutiu.provider.RpcServer;
import com.tiutiu.provider.RpcServicePostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@ComponentScan
@Configuration
@Import(RpcServicePostProcessor.class)
public class Provider {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(Provider.class);
        RpcServer rpcServer = new RpcServer();
        rpcServer.start("localhost", 8088);
    }
}