package com.rinneohara.ssyx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/17 14:35
 */
@Configuration
public class ThreadPoolConfig {
    //定义一个线程池
    //参数分别含义为，活跃核心线程数，线程池最大数，活跃时间，活跃时间的单位，阻塞队列，线程池工厂
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(
                2,
                5,
                2,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(3),
                Executors.defaultThreadFactory()
        );
    }
}
