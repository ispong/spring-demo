package com.isxcode.demo.spring.aop.service;

import com.isxcode.demo.spring.aop.annotation.WatchDog;
import com.isxcode.demo.spring.aop.pojo.ReqDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DogService {

    /*
     * 支持动态传参
     *
     * @ispong
     */
    @WatchDog(who = "#reqDto.username")
    public void testWatchDog(ReqDto reqDto) {

        log.info("4:reqDto {}", reqDto);
    }
}
