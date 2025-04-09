package com.project.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspect class for logging method executions in repositories, services, and controllers.
 * This class provides logging for method entry, successful return, and exception throwing.
 */
@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    /**
     * Logs the execution of methods in repository classes before they are invoked.
     *
     * @param joinPoint The join point representing the method execution.
     */
    @Before("execution(* com.project.repositories.*.*(..))")
    public void beforeRepositories(JoinPoint joinPoint) {
        loggerInfoBefore(joinPoint);
    }
    /**
     * Logs the execution of methods in service classes before they are invoked.
     *
     * @param joinPoint The join point representing the method execution.
     */
    @Before("execution(* com.project.service.*.*(..))")
    public void beforeService(JoinPoint joinPoint) {
        loggerInfoBefore(joinPoint);
    }

    /**
     * Logs the execution of methods in controller classes before they are invoked.
     *
     * @param joinPoint The join point representing the method execution.
     */
    @Before("execution(* com.project.controller.*.*(..))")
    public void beforeController(JoinPoint joinPoint) {
        loggerInfoBefore(joinPoint);
    }

    /**
     * Logs the method name and declaring type before method execution.
     *
     * @param joinPoint The join point representing the method execution.
     */
    private void loggerInfoBefore(JoinPoint joinPoint) {
        logger.info("Executing method: {}.{}", joinPoint.getSignature().getName(), joinPoint.getSignature().getDeclaringTypeName());
    }

    /**
     * Logs the successful return of methods in repository classes.
     *
     * @param joinPoint The join point representing the method execution.
     * @param result    The result returned by the method.
     */
    @AfterReturning(pointcut = "execution(* com.project.repositories.*.*(..))", returning = "result")
    public void afterReturningRepositories(JoinPoint joinPoint, Object result) {
        loggerInfoAfterReturning(joinPoint, result);
    }

    /**
     * Logs the successful return of methods in service classes.
     *
     * @param joinPoint The join point representing the method execution.
     * @param result    The result returned by the method.
     */
    @AfterReturning(pointcut = "execution(* com.project.service.*.*(..))", returning = "result")
    public void afterReturningService(JoinPoint joinPoint, Object result) {
        loggerInfoAfterReturning(joinPoint, result);
    }

    /**
     * Logs the successful return of methods in controller classes.
     *
     * @param joinPoint The join point representing the method execution.
     * @param result    The result returned by the method.
     */
    @AfterReturning(pointcut = "execution(* com.project.controller.*.*(..))", returning = "result")
    public void afterReturningController(JoinPoint joinPoint, Object result) {
        loggerInfoAfterReturning(joinPoint, result);
    }

    /**
     * Logs the method name, declaring type, and result after successful method execution.
     *
     * @param joinPoint The join point representing the method execution.
     * @param result    The result returned by the method.
     */
    private void loggerInfoAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Method executed successfully: {}.{}", joinPoint.getSignature().getName(), joinPoint.getSignature().getDeclaringTypeName());
        if (result != null && result.toString().length() < 1000) {
            logger.info("Method result: {}", result);
        }
    }

    /**
     * Logs exceptions thrown by methods in repository classes.
     *
     * @param joinPoint The join point representing the method execution.
     * @param error     The exception thrown by the method.
     */
    @AfterThrowing(pointcut = "execution(* com.project.repositories.*.*(..))", throwing = "error")
    public void afterThrowingRepositories(JoinPoint joinPoint, Throwable error) {
        loggerInfoAfterThrowing(joinPoint, error);
    }

    /**
     * Logs exceptions thrown by methods in service classes.
     *
     * @param joinPoint The join point representing the method execution.
     * @param error     The exception thrown by the method.
     */
    @AfterThrowing(pointcut = "execution(* com.project.service.*.*(..))", throwing = "error")
    public void afterThrowingService(JoinPoint joinPoint, Throwable error) {
        loggerInfoAfterThrowing(joinPoint, error);
    }

    /**
     * Logs exceptions thrown by methods in controller classes.
     *
     * @param joinPoint The join point representing the method execution.
     * @param error     The exception thrown by the method.
     */
    @AfterThrowing(pointcut = "execution(* com.project.controller.*.*(..))", throwing = "error")
    public void afterThrowingController(JoinPoint joinPoint, Throwable error) {
        loggerInfoAfterThrowing(joinPoint, error);
    }

    /**
     * Logs the method name, declaring type, and exception details after method execution failure.
     *
     * @param joinPoint The join point representing the method execution.
     * @param error     The exception thrown by the method.
     */
    private void loggerInfoAfterThrowing(JoinPoint joinPoint, Throwable error) {
        logger.error("Method execution failed: {}.{}", joinPoint.getSignature().getName(), joinPoint.getSignature().getDeclaringTypeName());
        logger.error("Error: {}", error.getMessage());
    }
}