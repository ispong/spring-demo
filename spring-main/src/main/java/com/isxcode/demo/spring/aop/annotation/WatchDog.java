package com.isxcode.demo.spring.aop.annotation;

import java.lang.annotation.*;

@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface WatchDog {

    String who() default "ispong";
}
