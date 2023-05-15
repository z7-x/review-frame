package com.z7.bespoke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.spring.annotation.MapperScan;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * 项目名称：review-frame
 * 类 名 称：UserAuthApplication
 * 类 描 述：TODO 用户权限启动类
 * 创建时间：2023/4/3 4:00 下午
 *
 * @author z7
 */
@EnableEurekaClient
@RestController
@ComponentScan(basePackages = {"com.z7.bespoke.*"})//开启包扫描
@MapperScan(basePackages = {"com.z7.bespoke.mapper"})//开启扫描mybatis包
//(exclude = DataSourceAutoConfiguration.class)//禁止 SpringBoot 自动注入数据源配置
@SpringBootApplication
public class UserAuthApplication {

    static Logger logger = LoggerFactory.getLogger(UserAuthApplication.class);

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(UserAuthApplication.class, args);
        Environment env = application.getEnvironment();
        logger.info("\n----------------------------------------------------------\n\t" +
                        "Application '{}' is running! Access URLs:\n\t" +
                        "Local: \t\thttp://localhost:{}\n\t" +
                        "External: \t\thttp://{}:{}\n\t" +
                        "Doc: \t\thttp://{}:{}/doc.html\n" +
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
    }

    @Value("${foo}")
    private String foo;

    @RequestMapping(value = "/foo")
    public String hi() {
        System.out.println(foo);
        return foo;
    }
}