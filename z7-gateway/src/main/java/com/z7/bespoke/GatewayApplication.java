package com.z7.bespoke;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.spring.annotation.MapperScan;


/**
 * 项目名称：review-frame
 * 类 名 称：GateWayApplication
 * 类 描 述：TODO 网关配置启动类
 * 创建时间：2023/4/8 5:55 下午
 *
 * @author z7
 */
@Slf4j
/*开启包扫描*/
@ComponentScan(basePackages = {"com.z7.bespoke.*"})
/*开启扫描mybatis包*/
@MapperScan(basePackages = {"com.z7.bespoke.mapper"})
@RestController
@EnableDiscoveryClient
@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Value("${foo}")
    private String foo;

    @RequestMapping(value = "/foo")
    public String hi() {
        log.info("测试配置文件是否读取:{}", foo);
        return foo;
    }
}
