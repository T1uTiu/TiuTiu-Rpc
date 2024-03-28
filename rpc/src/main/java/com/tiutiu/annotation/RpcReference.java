package com.tiutiu.annotation;

import com.tiutiu.common.constant.LoadBalanceRule;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {
    String version() default "1.0";
    long timeout() default 5000;
    int retryCount() default 5;
    String loadBalanceRule() default LoadBalanceRule.ConsistentHash;
}
