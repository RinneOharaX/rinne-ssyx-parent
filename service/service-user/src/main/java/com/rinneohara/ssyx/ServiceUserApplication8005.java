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
 * @DATE: 2023/9/5 13:38
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@ComponentScan("com.rinneohara.ssyx.*")
@MapperScan("com.rinneohara.ssyx.mapper")
public class ServiceUserApplication8005 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication8005.class,args);
    }
}
