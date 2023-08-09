package com.rinneohara.ssyx;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/8 14:20
 */

@EnableSwagger2WebMvc
@SpringBootApplication
@ComponentScan("com.rinneohara.ssyx.*")
@MapperScan("com.rinneohara.ssyx.mapper")
public class ServiceSysApplication8001 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceSysApplication8001.class,args);
    }
}
