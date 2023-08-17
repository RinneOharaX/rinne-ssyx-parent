package com.rinneohara.ssyx;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/16 11:24
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableKnife4j
@EnableRabbit
@ComponentScan("com.rinneohara.ssyx.*")
@EnableFeignClients
public class ServiceSearchApplication8003 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceSearchApplication8003.class,args);
    }
}
