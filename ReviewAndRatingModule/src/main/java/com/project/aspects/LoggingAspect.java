package com.project.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* com.project.repositories.*.*(..))")
    public void beforeRepositories(JoinPoint joinPoint) {
        loggerInfoBefore(joinPoint);
    }

    @Before("execution(* com.project.service.*.*(..))")
    public void beforeService(JoinPoint joinPoint) {
        loggerInfoBefore(joinPoint);
    }

    @Before("execution(* com.project.controller.*.*(..))")
    public void beforeController(JoinPoint joinPoint) {
        loggerInfoBefore(joinPoint);
    }

    private void loggerInfoBefore(JoinPoint joinPoint) {
        logger.info("Executing method: {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "execution(* com.project.repositories.*.*(..))", returning = "result")
    public void afterReturningRepositories(JoinPoint joinPoint, Object result) {
        loggerInfoAfterReturning(joinPoint, result);
    }

    @AfterReturning(pointcut = "execution(* com.project.service.*.*(..))", returning = "result")
    public void afterReturningService(JoinPoint joinPoint, Object result) {
        loggerInfoAfterReturning(joinPoint, result);
    }

    @AfterReturning(pointcut = "execution(* com.project.controller.*.*(..))", returning = "result")
    public void afterReturningController(JoinPoint joinPoint, Object result) {
        loggerInfoAfterReturning(joinPoint, result);
    }

    private void loggerInfoAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Method executed successfully: {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        if (result != null && result.toString().length() < 1000) {
            logger.info("Method result: {}", result);
        }
    }

    @AfterThrowing(pointcut = "execution(* com.project.repositories.*.*(..))", throwing = "error")
    public void afterThrowingRepositories(JoinPoint joinPoint, Throwable error) {
        loggerInfoAfterThrowing(joinPoint, error);
    }

    @AfterThrowing(pointcut = "execution(* com.project.service.*.*(..))", throwing = "error")
    public void afterThrowingService(JoinPoint joinPoint, Throwable error) {
        loggerInfoAfterThrowing(joinPoint, error);
    }

    @AfterThrowing(pointcut = "execution(* com.project.controller.*.*(..))", throwing = "error")
    public void afterThrowingController(JoinPoint joinPoint, Throwable error) {
        loggerInfoAfterThrowing(joinPoint, error);
    }

    private void loggerInfoAfterThrowing(JoinPoint joinPoint, Throwable error) {
        logger.error("Method execution failed: {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        logger.error("Error: {}", error.getMessage());
    }
}