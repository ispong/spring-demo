package com.isxcode.demo.spring.aop.annotation;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class WatchDogAspect {

    /*
     * 指定需要解析的注解
     *
     * @ispong
     */
    @Pointcut("@annotation(com.isxcode.demo.spring.aop.annotation.WatchDog)")
    public void watchDogOperate() {}

    @Before(value = "watchDogOperate()&&@annotation(watchDog)")
    public void before(JoinPoint joinPoint, WatchDog watchDog) {

        log.info("2:before");
        String username = spelParseUsername(joinPoint, watchDog.who());
        log.info("3:username {}", username);
    }

    @AfterReturning(value = "watchDogOperate()&&@annotation(watchDog)")
    public void afterReturning(JoinPoint joinPoint, WatchDog watchDog) {

        log.info("5:afterReturning");
    }

    @AfterThrowing(value = "watchDogOperate()&&@annotation(watchDog)")
    public void afterThrowing(JoinPoint joinPoint, WatchDog watchDog) {

        log.info("afterThrowing");
    }

    @After(value = "watchDogOperate()&&@annotation(watchDog)")
    public void after(JoinPoint joinPoint, WatchDog watchDog) {

        log.info("6:after");
    }

    @Around(value = "watchDogOperate()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {

        log.info("1:around");
        // 开始执行service中的方法
        Object retVal = pjp.proceed();
        log.info("7:around");
        return retVal;
    }

    /*
     * spring表达式解析
     *
     * @ispong
     */
    public String spelParseUsername(JoinPoint joinPoint, String expressionStr) {

        if (expressionStr.isEmpty()) {
            return "";
        }

        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        String[] params =
                new LocalVariableTableParameterNameDiscoverer()
                        .getParameterNames(((MethodSignature) joinPoint.getSignature()).getMethod());
        if (params == null || params.length < 1) {
            return "";
        }

        for (String paramName : params) {
            context.setVariable(paramName, joinPoint.getArgs()[paramName.indexOf(paramName)]);
        }

        return (String) parser.parseExpression(expressionStr).getValue(context);
    }
}
