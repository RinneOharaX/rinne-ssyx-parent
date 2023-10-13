package com.rinneohara.ssyx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/10/7 12:56
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
//@MapperScan("com.rinneohara.ssyx.mapper.*")
@ComponentScan("com.rinneohara.ssyx.*")
@MapperScan("com.rinneohara.ssyx.mapper")
public class ServiceOrderApplication8008 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOrderApplication8008.class,args);
    }
}
