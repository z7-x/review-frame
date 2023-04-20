package com.z7.bespoke;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 项目名称：review-frame
 * 类 名 称：GateWayApplication
 * 类 描 述：TODO 网关配置启动类
 * 创建时间：2023/4/8 5:55 下午
 *
 * @author z7
 */
@RestController
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableEurekaClient
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Value("${foo}")
    String foo;

    @RequestMapping(value = "/foo")
    public String hi() {
        return foo;
    }
}
