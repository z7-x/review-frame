package com.z7.bespoke.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.support.NameUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


/**
 * 项目名称：review-frame
 * 类 名 称：Knife4jGatewayConfig
 * 类 描 述：TODO  网关knife4j配置
 * 创建时间：2023/5/26 17:12 下午
 * 创 建 人：z7
 */
@RestController
public class Knife4jGatewayConfig {

    private final SecurityConfiguration securityConfiguration;

    private final UiConfiguration uiConfiguration;

    private final SwaggerResourceAdapter swaggerResourceAdapter;

    public Knife4jGatewayConfig(@Autowired(required = false) SecurityConfiguration securityConfiguration,
                                @Autowired(required = false) UiConfiguration uiConfiguration,
                                SwaggerResourceAdapter swaggerResourceAdapter) {
        this.securityConfiguration = securityConfiguration;
        this.uiConfiguration = uiConfiguration;
        this.swaggerResourceAdapter = swaggerResourceAdapter;
    }

    /**
     * 安全配置
     */
    @GetMapping("/swagger-resources/configuration/security")
    public Mono<ResponseEntity<SecurityConfiguration>> securityConfiguration() {
        return Mono.just(new ResponseEntity<>(
                Optional.ofNullable(securityConfiguration).orElse(SecurityConfigurationBuilder.builder().build()), HttpStatus.OK));
    }

    /**
     * ui配置
     */
    @GetMapping("/swagger-resources/configuration/ui")
    public Mono<ResponseEntity<UiConfiguration>> uiConfiguration() {
        return Mono.just(new ResponseEntity<>(
                Optional.ofNullable(uiConfiguration).orElse(UiConfigurationBuilder.builder().build()), HttpStatus.OK));
    }

    /**
     * 资源配置，自动路由到微服务中的各个服务的api-docs信息
     */
    @GetMapping("/swagger-resources")
    public Mono<ResponseEntity<List<SwaggerResource>>> swaggerResources() {
        return Mono.just(new ResponseEntity<>(swaggerResourceAdapter.get(), HttpStatus.OK));
    }

    /**
     * favicon.ico
     */
    @GetMapping("/favicon.ico")
    public Mono<ResponseEntity<?>> favicon() {
        return Mono.just(new ResponseEntity<>(null, HttpStatus.OK));
    }

    /**
     * swagger资源适配器
     *
     * @author <font size = "20" color = "#3CAA3C"><a href="https://gitee.com/JustryDeng">JustryDeng</a></font> <img src="https://gitee.com/JustryDeng/shared-files/raw/master/JustryDeng/avatar.jpg" />
     * @since 2021.0.1.D
     */
    @Slf4j
    @Primary
    @Component
    public static class SwaggerResourceAdapter implements SwaggerResourcesProvider {

        /**
         * spring-cloud-gateway是否开启了根据服务发现自动为服务创建router
         * false:从数据库中读取
         * true:从对应的微服务中去读取server.servlet.context-path
         */
        @Value("${spring.cloud.gateway.discovery.locator.enabled:false}")
        private boolean autoCreateRouter;

        @Value("${spring.application.name:}")
        private String applicationName;

        @Resource
        private RouteLocator routeLocator;

        @Resource
        private GatewayProperties gatewayProperties;

        /**
         * 根据当前所有的微服务路由信息，创建对应的SwaggerResource
         */
        @Override
        public List<SwaggerResource> get() {
            List<SwaggerResource> finalResources;
            Set<String> routes = new LinkedHashSet<>(16);
            // 获取所有路由的id
            routeLocator.getRoutes().subscribe(route -> {
                String routeId = route.getId();
                if (routeId.contains("DiscoveryClient")) {
                    routeId = routeId.split("_")[1].toLowerCase(Locale.ROOT);
                }
                /*routeId = routeId.replace("ReactiveCompositeDiscoveryClient_", "");*/
                routes.add(routeId);
            });
            // 没有开启自动创建路由，那么走配置文件中配置的路由
            if (!autoCreateRouter) {
                finalResources = new ArrayList<>(16);
                gatewayProperties.getRoutes().stream()
                        // 过滤出配置文件中定义的路由
                        .filter(routeDefinition -> routes.contains(routeDefinition.getId())).forEach(route -> {
                    route.getPredicates().stream()
                            // 过滤出设置有Path Predicate的路由
                            .filter(predicateDefinition -> ("Path").equalsIgnoreCase(predicateDefinition.getName()))
                            // 根据路径拼接成api-docs路径,生成SwaggerResource
                            .forEach(predicateDefinition -> finalResources.add(swaggerResource(route.getId(),
                                    predicateDefinition.getArgs().get(NameUtils.GENERATED_NAME_PREFIX + "0")
                                            // 如果对应的微服务设置了server.servlet.context-path，那么这里应该是{context-path}/v3/api-docs
                                            .replace("**", "v3/api-docs"))));
                });
            } else {
                // 如果对应的微服务设置了server.servlet.context-path，那么这里应该是/{context-path}/v3/api-docs
                finalResources = routes.stream().map(routeId -> swaggerResource(routeId, routeId + "/v3/api-docs")).collect(Collectors.toList());
            }
            List<SwaggerResource> resources = new ArrayList<>(finalResources);
            // resources过滤掉网关的SwaggerResource, 我们一般也不会在网关中编写业务controller
            if (StringUtils.isNotBlank(applicationName)) {
                resources = resources.stream().filter(x -> !applicationName.equalsIgnoreCase(x.getName())).collect(Collectors.toList());
            }
            // 这里你需要处理一下


            // 排序
            resources.sort(Comparator.comparing(x -> x.getName().length()));
            return resources;
        }

        /**
         * 创建swagger资源
         *
         * @param name     swagger资源名(注：一般对应 {路由id})
         * @param location swagger资源路径(注：一般对应 {路由id}/v3/api-docs)
         * @return swagger资源
         */
        private SwaggerResource swaggerResource(String name, String location) {
            // 假设你的微服务是 user-auth，那么 正常来讲  传入这里的  name=user-auth   location=user-auth/v3/api-docs
            // 但是现在看着不是， 所以你要debug刚才哪个位置， 处理一下
            /*if (name.contains("CompositeDiscoveryClient")) {
                name = name.split("_")[1].toLowerCase(Locale.ROOT);
            }
            if (location.contains("CompositeDiscoveryClient")) {
                location = location.split("_")[1].toLowerCase(Locale.ROOT);
            }*/
            location = location.startsWith("/") ? location.substring(1) : location;
            log.info("name:{},location:{}", name, location);
            SwaggerResource swaggerResource = new SwaggerResource();
            swaggerResource.setName(name);
            swaggerResource.setLocation(location + "?group=" + name); // 保证这里的location是：「服务名」/v3/api-docs?group=「服务名」
            swaggerResource.setSwaggerVersion("3.0");
            return swaggerResource;
        }
    }
}