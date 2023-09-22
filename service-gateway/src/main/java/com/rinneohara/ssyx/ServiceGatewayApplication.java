package com.rinneohara.ssyx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/29 12:52
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.rinneohara.ssyx.*")
public class ServiceGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceGatewayApplication.class,args);
    }
}
