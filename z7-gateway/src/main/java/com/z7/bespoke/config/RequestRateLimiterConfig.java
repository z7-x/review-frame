package com.z7.bespoke.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

/**
 * 项目名称：review-frame
 * 类 名 称：RouterFunctionConfiguration
 * 类 描 述：TODO  限流策略:根据配置路由参数 对IP限流 还可对接口或者用户限流 后期根据需求添加
 * 创建时间：2023/4/25 17:31 下午
 * 创 建 人：z7
 */
@Configuration
public class RequestRateLimiterConfig {
    @Bean
    public KeyResolver remoteAddrKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
    }

}
