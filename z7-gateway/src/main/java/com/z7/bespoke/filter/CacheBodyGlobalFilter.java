package com.z7.bespoke.filter;

import com.z7.bespoke.properties.GlobalSignatureProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

/**
 * 项目名称：review-frame
 * 类 名 称：CacheBodyGlobalFilter
 * 类 描 述：TODO 缓存请求body参数
 * 创建时间：2023/4/24 2:52 下午
 * 创 建 人：z7
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheBodyGlobalFilter implements Ordered, GlobalFilter {

    private final GlobalSignatureProperties globalSignatureConfigProperties;
    private final static AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!globalSignatureConfigProperties.isEnabled() || CollectionUtils.isEmpty(globalSignatureConfigProperties.getKeyDetails())
                || CollectionUtils.isEmpty(globalSignatureConfigProperties.getNeedFilterUrlPatterns())) {
            return chain.filter(exchange);
        }
        //请求的资源路径
        String rawPath = exchange.getRequest().getURI().getRawPath();
        for (String pattern : globalSignatureConfigProperties.getNoNeedFilterUrlPatterns()) {
            boolean match = antPathMatcher.match(pattern, rawPath);
            if (match) {
                return chain.filter(exchange);
            }
        }

        //查看是否在拦截内
        boolean matched = false;
        List<String> needFilterUrlPatterns = globalSignatureConfigProperties.getNeedFilterUrlPatterns();
        for (String needFilterUrlPattern : needFilterUrlPatterns) {
            matched = antPathMatcher.match(needFilterUrlPattern, rawPath);
            if (matched) {
                break;
            }
        }
        if (!matched) {
            return chain.filter(exchange);
        }
        if (Objects.equals(exchange.getRequest().getMethod(), HttpMethod.GET)) {
            return chain.filter(exchange);
        } else {
            MediaType contentType = exchange.getRequest().getHeaders().getContentType();
            if (contentType == null) {
                return chain.filter(exchange);
            }
            if (contentType.getType().startsWith("multipart")) {
                return chain.filter(exchange);
            }
            return DataBufferUtils.join(exchange.getRequest().getBody())
                    .flatMap(dataBuffer -> {
                        System.out.println(dataBuffer.capacity());
                        DataBufferUtils.retain(dataBuffer);
                        Flux<DataBuffer> cachedFlux = Flux
                                .defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
                        ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(
                                exchange.getRequest()) {
                            @Override
                            public Flux<DataBuffer> getBody() {
                                return cachedFlux;
                            }
                        };
                        // 释放slice未回收的dataBuffer
//                        DataBufferUtils.release(dataBuffer);
                        return chain.filter(exchange.mutate().request(mutatedRequest).build());
                    });
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
