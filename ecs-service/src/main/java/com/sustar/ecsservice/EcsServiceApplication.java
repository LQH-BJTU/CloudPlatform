package com.sustar.ecsservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ECS云服务器配置服务启动类
 */
@SpringBootApplication
@MapperScan("com.sustar.ecsservice.mapper")
public class EcsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcsServiceApplication.class, args);
    }
}
