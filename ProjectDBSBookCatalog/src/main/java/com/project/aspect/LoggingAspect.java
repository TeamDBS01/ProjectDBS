package com.project.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Pointcut for all methods in controllers
    @Pointcut("execution(* com.project.controller..*(..))")
    public void controllerMethods() {}

    // Pointcut for all methods in services
    @Pointcut("execution(* com.project.service..*(..))")
    public void serviceMethods() {}

    // Pointcut for all methods in repositories
    @Pointcut("execution(* com.project.repositories..*(..))")
    public void repositoryMethods() {}

    @Before("controllerMethods() || serviceMethods() || repositoryMethods()")
    public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        logger.info("Executing method: {}.{}", className, methodName);
    }

    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, ResponseEntity<?> result) {
        int statusCode = result.getStatusCode().value();
        logger.info("HTTP Status: {} - Method: {}", statusCode, joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "controllerMethods() || serviceMethods() || repositoryMethods()", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        logger.error("Method execution failed:{}", error.getMessage());
    }

}
