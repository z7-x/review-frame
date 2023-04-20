package com.z7.bespoke;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;


/**
 * 项目名称：review-frame
 * 类 名 称：ConfigApplication
 * 类 描 述：TODO 配置服务启动类
 * 创建时间：2023/4/8 5:13 下午
 * @author z7
 */
@EnableDiscoveryClient
@EnableConfigServer
@SpringBootApplication
public class ConfigApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigApplication.class, args);
    }
}