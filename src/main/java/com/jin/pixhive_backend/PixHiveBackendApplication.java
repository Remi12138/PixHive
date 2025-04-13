package com.jin.pixhive_backend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan("com.jin.pixhive_backend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class PixHiveBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PixHiveBackendApplication.class, args);
    }

}
