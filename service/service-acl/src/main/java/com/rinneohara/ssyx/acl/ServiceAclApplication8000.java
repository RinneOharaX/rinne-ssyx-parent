package com.rinneohara.ssyx.acl;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @PROJECT_NAME: rinne-ssyx-parent
 * @DESCRIPTION:
 * @USER: Administrator
 * @DATE: 2023/8/1 13:00
 */



@EnableSwagger2WebMvc
@SpringBootApplication
@ComponentScan("com.rinneohara.ssyx.*")

public class ServiceAclApplication8000 {
    public static void main(String[] args) {
        SpringApplication.run(ServiceAclApplication8000.class,args);
    }

}
