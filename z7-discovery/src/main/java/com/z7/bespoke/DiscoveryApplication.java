package com.z7.bespoke;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * 项目名称：review-frame
 * 类 名 称：DiscoveryApplication
 * 类 描 述：TODO 注册服务启动类
 * 创建时间：2023/3/27 11:10 上午
 * @author z7
 */
@EnableEurekaServer
@SpringBootApplication
public class DiscoveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryApplication.class, args);
    }
}