package com.crrc.sys;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * 启动类
 *
 * @author 谭永强
 * @date 2022-08-02
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.crrc.sys.mapper")
@EnableFeignClients("com.crrc")
@ComponentScan("com.crrc")
public class SysServerRun {

    public static void main(String[] args) {
        SpringApplication.run(SysServerRun.class, args);
    }
}
