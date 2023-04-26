package com.z7.bespoke.service.impl;

import com.alibaba.fastjson.JSON;
import com.z7.bespoke.mapper.ApiKeyDetailMapper;
import com.z7.bespoke.mapper.GlobalSignaturePropertiesMapper;
import com.z7.bespoke.mapper.RouteDefinitionMapper;
import com.z7.bespoke.mapper.po.ApiKeyDetail;
import com.z7.bespoke.mapper.po.RouteDefinition;
import com.z7.bespoke.properties.GlobalSignatureProperties;
import com.z7.bespoke.repository.CacheRouteDefinitionRepository;
import com.z7.bespoke.service.IGatewayService;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.*;


/**
 * 项目名称：review-frame
 * 类 名 称：GatewayServiceImpl
 * 类 描 述：TODO 从DB中拿到数据库路由配置 构建路由信息 缓存写入
 * 创建时间：2023/4/26 10:42 上午
 * 创 建 人：z7
 */
@Service
@Transactional
@RequiredArgsConstructor
public class GatewayServiceImpl implements IGatewayService, ApplicationEventPublisherAware, CommandLineRunner {
    private Logger log = LoggerFactory.getLogger(GatewayServiceImpl.class);

    private final CacheRouteDefinitionRepository routeDefinitionWriter;
    private final RouteDefinitionMapper routeDefinitionMapper;
    private final GlobalSignaturePropertiesMapper globalSignaturePropertiesMapper;
    private final GlobalSignatureProperties globalSignatureConfigProperties;
    private final ApiKeyDetailMapper apiKeyDetailMapper;

    private ApplicationEventPublisher publisher;

    @Override
    public void run(String... args) throws Exception {
        this.save();
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }


    public void deleteRoute(String routeId) {
        log.info("网关配置，删除路由id=====>" + routeId);
        routeDefinitionWriter.delete(Mono.just(routeId)).subscribe();
    }

    public void save() {
        List<RouteDefinition> gatewayRouteList = routeDefinitionMapper.selectAll();
        log.info("网关配置信息：=====>" + JSON.toJSONString(gatewayRouteList));
        gatewayRouteList.forEach(gatewayRoute -> {
            RouteDefinition definition = new RouteDefinition();
            Map<String, String> predicateParams = new HashMap<>(8);
            PredicateDefinition predicate = new PredicateDefinition();
            FilterDefinition filterDefinition = new FilterDefinition();
            Map<String, String> filterParams = new HashMap<>(8);

            definition.setId(gatewayRoute.getId());
            predicate.setName("Path");
            predicateParams.put("pattern", gatewayRoute.getPath());
            predicateParams.put("pathPattern", gatewayRoute.getPath());
            URI uri = UriComponentsBuilder.fromUriString("lb://" + gatewayRoute.getServiceId()).build().toUri();

            /*去前缀*/
            filterDefinition.setName("StripPrefix");
            /*去前缀位数*/
            filterParams.put("_genkey_0", gatewayRoute.getStripPrefix());
            /*令牌桶容量 允许用户每秒处理多少个请求*/
            filterParams.put("redis-rate-limiter.replenishRate", gatewayRoute.getLimiterRate());
            /*限流策略(#{@BeanName}) 允许在一秒钟内完成的最大请求数*/
            filterParams.put("redis-rate-limiter.burstCapacity", gatewayRoute.getLimiterCapacity());
            /*使用SpEL按名称引用bean*/
            filterParams.put("key-resolver", "#{@remoteAddrKeyResolver}");

            predicate.setArgs(predicateParams);
            filterDefinition.setArgs(filterParams);

            definition.setPredicates(Arrays.asList(predicate));
            definition.setFilters(Arrays.asList(filterDefinition));
            definition.setUri(uri);

            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
        });
        this.publisher.publishEvent(new RefreshRoutesEvent(this));

        // 接口从数据库获取,统一第三方系统调用内部接口全局配置
        List<com.z7.bespoke.mapper.po.GlobalSignatureProperties> globalSignatureProperties = globalSignaturePropertiesMapper.queryGlobalSignatureProperties();
        if (CollectionUtils.isEmpty(globalSignatureProperties)) {
            log.info("暂未配置全局签名验证配置:", globalSignatureProperties.size());
            return;
        }
        com.z7.bespoke.mapper.po.GlobalSignatureProperties properties = globalSignatureProperties.get(0);
        if (null != globalSignatureProperties) {
            if (null != properties.getEnabled()) {
                globalSignatureConfigProperties.setEnabled(properties.getEnabled());
            }
            if (StringUtils.isNotEmpty(properties.getAuthorizationHeaderName())) {
                globalSignatureConfigProperties.setAuthorizationHeaderName(properties.getAuthorizationHeaderName());
            }
            if (StringUtils.isNotEmpty(properties.getApiKeyHeaderName())) {
                globalSignatureConfigProperties.setApiKeyHeaderName(properties.getApiKeyHeaderName());
            }
            if (StringUtils.isNotBlank(properties.getNoNeedFilterUrlPatterns())) {
                globalSignatureConfigProperties.setNoNeedFilterUrlPatterns(Arrays.asList(properties.getNoNeedFilterUrlPatterns().split(",")));
            }
            if (StringUtils.isNotBlank(properties.getNeedFilterUrlPatterns())) {
                globalSignatureConfigProperties.setNeedFilterUrlPatterns(Arrays.asList(properties.getNeedFilterUrlPatterns().split(",")));
            }
        }

        List<ApiKeyDetail> apiKeyDetails = apiKeyDetailMapper.queryApiKeyDetails();
        if (!CollectionUtils.isEmpty(apiKeyDetails)) {
            List<com.z7.bespoke.properties.GlobalSignatureProperties.KeyDetail> detailList = new ArrayList<>();
            apiKeyDetails.forEach(apiKeyDetail -> {
                com.z7.bespoke.properties.GlobalSignatureProperties.KeyDetail detail = new com.z7.bespoke.properties.GlobalSignatureProperties.KeyDetail();
                BeanUtils.copyProperties(apiKeyDetail, detail);
                detailList.add(detail);
            });
            globalSignatureConfigProperties.setKeyDetails(detailList);
        }
    }
}