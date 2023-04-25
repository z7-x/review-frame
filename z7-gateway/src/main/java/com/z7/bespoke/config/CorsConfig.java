package com.z7.bespoke.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

/**
 * 项目名称：review-frame
 * 类 名 称：CorsConfig
 * 类 描 述：TODO 网关解决跨域问题
 * 创建时间：2023/4/24 11:50 上午
 * 创 建 人：z7
 */
@Configuration
public class CorsConfig {

    /**
     * @description: 跨域问题
     */
    @Bean
    public CorsWebFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        /*是否允许请求带有验证信息*/
        config.setAllowCredentials(true);
        /*允许访问的方法名,GET POST等*/
        config.addAllowedMethod("*");
        /*允许访问的客户端域名*/
        config.addAllowedOrigin("*");
        /*允许服务端访问的客户端请求头*/
        config.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}
