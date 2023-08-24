package com.rinneohara.ssyx;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/18 13:54
 */

@SpringBootApplication
@EnableKnife4j
@ComponentScan("com.rinneohara.ssyx.*")
@EnableFeignClients
@EnableDiscoveryClient
public class ServiceActivityApplication8004 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceActivityApplication8004.class,args);
    }
}
