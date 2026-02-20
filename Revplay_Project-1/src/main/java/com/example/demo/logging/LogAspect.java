package com.example.demo.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {

    @Before("execution(* com.example.demo.controller..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("API Called: " + joinPoint.getSignature().toShortString());
    }
}
