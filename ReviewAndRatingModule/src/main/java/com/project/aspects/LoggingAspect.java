package com.project.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("execution(* com.project.*.*.*(..))")
    public void beforeMethod(JoinPoint joinPoint) {
        log.info("Executing method: {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "execution(* com.project.*.*.*(..))", returning = "result")
    public void afterReturningMethod(JoinPoint joinPoint, Object result) {
        log.info("Method executed successfully: {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        if (result != null && result.toString().length() < 1000) {
            log.info("Method result: {}", result);
        }
    }

    @AfterThrowing(pointcut = "execution(* com.project.*.*.*(..))", throwing = "error")
    public void afterThrowingMethod(JoinPoint joinPoint, Throwable error) {
        log.error("Method execution failed: {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        log.error("Error: {}", error.getMessage());
    }
}