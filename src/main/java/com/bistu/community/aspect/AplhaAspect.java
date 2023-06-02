package com.bistu.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class AplhaAspect {

    /**
     * 通过注解的形式定义切点
     * 括号中第一个*代表的是所有类型的返回值
     * 后面是包名，包名后的*表示的是这个包下的所有类.所有方法
     * (..)表示所有的参数
     */
    @Pointcut("execution(* com.bistu.community.service.*.*(..))")
    public void pointcut() {
    }

    /**
     * 使用注解的形式定义通知（advice）
     */
    @Before("pointcut()")
    public void before() {
        System.out.println("before");
    }

    @After("pointcut()")
    public void after() {
        System.out.println("after");
    }

    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("afterReturning");
    }

    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // joinPoint.proceed();调需要处理的目标组件的方法
        System.out.println("around before");
        Object obj = joinPoint.proceed();
        System.out.println("around after");
        return obj;
    }
}
