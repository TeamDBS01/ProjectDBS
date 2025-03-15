package com.project.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.project.services..*(..))")
    public void serviceLayer() {}

    @Pointcut("execution(* com.project.controller..*(..))")
    public void controllerLayer() {}

    @Before("serviceLayer()")
    public void beforeServiceMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.info("Entering Service Method: {} with arguments: {}", methodName, args);
    }

    @AfterReturning(pointcut = "serviceLayer()", returning = "result")
    public void afterServiceMethod(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        log.info("Service Method: {} executed successfully with result: {}", methodName, result);
    }

    @AfterThrowing(pointcut = "serviceLayer()", throwing = "exception")
    public void afterServiceMethodException(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        log.error("Service Method: {} threw an exception: {}", methodName, exception.getMessage());
    }

    @Before("controllerLayer()")
    public void beforeControllerMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.info("Entering Controller Method: {} with arguments: {}", methodName, args);
    }

    @AfterReturning(pointcut = "controllerLayer()", returning = "result")
    public void afterControllerMethod(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        log.info("Controller Method: {} executed successfully with result: {}", methodName, result);
    }

    @AfterThrowing(pointcut = "controllerLayer()", throwing = "exception")
    public void afterControllerMethodException(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        log.error("Controller Method: {} threw an exception: {}", methodName, exception.getMessage());
    }
}
