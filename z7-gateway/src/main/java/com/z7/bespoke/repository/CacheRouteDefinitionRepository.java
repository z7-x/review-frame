package com.z7.bespoke.repository;

import com.alibaba.fastjson.JSON;
import com.z7.bespoke.cache.RouteMapCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：review-frame
 * 类 名 称：GatewayController
 * 类 描 述：TODO 单服务使用MAP缓存路由 集群下使用REDIS
 *          TODO 实现RouteDefinitionRepository 替换gateway默认路由规则 实现通过redis保存获取路由功能
 * 创建时间：2023/4/25 3:42 下午
 * 创 建 人：z7
 */
@Slf4j
@Component
public class CacheRouteDefinitionRepository implements RouteDefinitionRepository {
    public static final String GATEWAY_ROUTES = "z7:gateway:routes";

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        List<RouteDefinition> routeDefinitions = new ArrayList<>();
        RouteMapCache.getValues().stream().forEach(route -> {
                    routeDefinitions.add(JSON.parseObject(route, RouteDefinition.class));
                }
        );
        return Flux.fromIterable(routeDefinitions);
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        log.info("保存路由信息:{}", route);
        return route.flatMap(routeDefinition -> {
            RouteMapCache.put(GATEWAY_ROUTES + routeDefinition.getId(), JSON.toJSONString(routeDefinition));
            return Mono.empty();
        });
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        log.info("删除路由信息{}", routeId);
        return routeId.flatMap(id -> {
            if (RouteMapCache.hasKey(GATEWAY_ROUTES + id)) {
                RouteMapCache.removeCache(GATEWAY_ROUTES + id);
                return Mono.empty();
            }
            return Mono.defer(() -> Mono.error(new NotFoundException("路由配置没有找到: " + routeId)));
        });
    }
}
