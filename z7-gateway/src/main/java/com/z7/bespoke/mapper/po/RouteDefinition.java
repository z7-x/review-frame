package com.z7.bespoke.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * 项目名称：review-frame
 * 类 名 称：RouteDefinition
 * 类 描 述：TODO 路由定义
 * 创建时间：2023/4/25 11:04 上午
 * 创 建 人：z7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "route_definition")
public class RouteDefinition extends org.springframework.cloud.gateway.route.RouteDefinition {

    /**
     * 映射服务
     */
    @Column(name = "service_id")
    private String serviceId;

    /**
     * 映射路径
     */
    @Column(name = "path_")
    private String path;

    /**
     * 映射外连接
     */
    @Column(name = "url_")
    private String url;

    /**
     * 令牌桶流速
     */
    @Column(name = "limiter_rate")
    private String limiterRate;

    /**
     * 令牌桶容量
     */
    @Column(name = "limiter_capacity")
    private String limiterCapacity;

    /**
     * 是否启用N/Y
     */
    @Column(name = "enabled_")
    private String enabled;

    /**
     * 是否忽略前缀
     */
    @Column(name = "strip_prefix")
    private String stripPrefix;

    /**
     * 排序
     */
    @Column(name = "route_order")
    private Integer routeOrder;
}