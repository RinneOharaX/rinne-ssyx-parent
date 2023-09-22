package com.rinneohara.ssyx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/9/20 15:31
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消数据源自动配置
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceCartApplication8007 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceCartApplication8007.class,args);
    }
}
