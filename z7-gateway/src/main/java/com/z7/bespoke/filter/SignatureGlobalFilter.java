package com.z7.bespoke.filter;

import com.alibaba.fastjson.JSON;
import com.z7.bespoke.commons.security.MD5Utils;
import com.z7.bespoke.properties.GlobalSignatureProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 项目名称：review-frame
 * 类 名 称：SignatureGlobalFilter
 * 类 描 述：TODO 签名校验全局过滤器
 * 创建时间：2023/4/24 3:42 下午
 * 创 建 人：z7
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SignatureGlobalFilter implements GlobalFilter, Ordered {

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

        VerifyResult verifyResult = this.doVerify(exchange);
        log.info("Signature verify result:" + JSON.toJSONString(verifyResult));
        if (verifyResult.isVerifySuccess()) {
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header("companyCode", verifyResult.getCompanyCode())
                    .build();
            return chain.filter(exchange.mutate().request(request).build());
        } else {
            ErrorResponseEntity responseEntity = new ErrorResponseEntity(401, verifyResult.getFailReason());
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(JSON.toJSONString(responseEntity).getBytes(StandardCharsets.UTF_8));
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
            return exchange.getResponse().writeWith(Flux.just(buffer));
        }
    }

    private VerifyResult doVerify(ServerWebExchange exchange) {
        VerifyResult result = new VerifyResult();
        result.setVerifySuccess(false);
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String apiKey = headers.getFirst(globalSignatureConfigProperties.getApiKeyHeaderName());
        log.info("request headers:{}", JSON.toJSONString(headers));
        String sign = headers.getFirst(globalSignatureConfigProperties.getAuthorizationHeaderName());
        AtomicReference<String> paramString = new AtomicReference<>("");
        String path = request.getURI().getRawPath();
        HttpMethod method = request.getMethod();
        log.info("request apiKey:" + apiKey);
        log.info("request signature:" + sign);
        log.info("request method:" + method);
        if (HttpMethod.GET.equals(method)) {
            paramString.set(this.createLinkString(request.getQueryParams().toSingleValueMap()));
        } else {
            paramString.set(resolveBodyFromRequest(exchange.getRequest()));
        }
        log.info(paramString.get());
        result.setParamString(paramString.get());
        //apikey必须
        if (StringUtils.isEmpty(apiKey)) {
            result.setFailReason(globalSignatureConfigProperties.getApiKeyHeaderName() + " missing");
            return result;
        }
        if (StringUtils.isEmpty(sign)) {
            //sign必须
            result.setFailReason(globalSignatureConfigProperties.getAuthorizationHeaderName() + " missing");
            return result;
        }
        //apikey是否合法
        String securityKey;
        String companyCode;
        Optional<com.z7.bespoke.properties.GlobalSignatureProperties.KeyDetail> detailOptional
                = globalSignatureConfigProperties.getKeyDetails().stream().filter(item -> item.getApiKey().equals(apiKey)).findFirst();
        if (detailOptional.isPresent()) {
            com.z7.bespoke.properties.GlobalSignatureProperties.KeyDetail keyDetail = detailOptional.get();
            securityKey = keyDetail.getSecurityKey();
            companyCode = keyDetail.getCompanyCode();
        } else {
            result.setFailReason(globalSignatureConfigProperties.getApiKeyHeaderName() + " invalid");
            return result;
        }
        String signStr = path + "|" + (paramString.get() == null ? "" : paramString.get()) + "|" + apiKey + "|" + securityKey;
        log.info("signStr:" + signStr);
        String serverSign = MD5Utils.sign(signStr, "UTF-8");
        log.info("密钥：server sign result:" + serverSign);
        if (!serverSign.equals(sign)) {
            result.setFailReason(globalSignatureConfigProperties.getAuthorizationHeaderName() + " invalid");
            return result;
        }
        result.setVerifySuccess(true);
        result.setCompanyCode(companyCode);
        return result;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class ErrorResponseEntity {
        private int customCode;
        private String message;
    }

    @Data
    public static class VerifyResult {
        private boolean verifySuccess;
        private String failReason;
        private String paramString;
        private String companyCode;
        private String originSignatureMsg;
    }

    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        //获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();

        AtomicReference<String> bodyRef = new AtomicReference<>();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            bodyRef.set(charBuffer.toString());
        });
        //获取request body
        return bodyRef.get();
    }

    private String createLinkString(Map<String, String> params) {
        if (params == null || params.size() == 0) {
            return "";
        }
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);

            if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符
                result.append(key).append("=").append(value);
            } else {
                result.append(key).append("=").append(value).append("&");
            }
        }
        return result.toString();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1200;
    }
}
