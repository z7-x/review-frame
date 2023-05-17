package com.z7.bespoke.frame.swagger.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 项目名称：review-frame
 * 类 名 称：Knife4jApiInfoProperties
 * 类 描 述：TODO api 基础信息配置。更多配置信息项见{@link Knife4jConfig}
 * 创建时间：2023/5/17 10:23 上午
 * 创 建 人：z7
 */
@Data
@Component
public class Knife4jApiInfoProperties {

    /**
     * 要扫描api的base包
     */
    @Value("${api-info.base-package:com.z7.bespoke}")
    private String basePackage;

    /**
     * 是否启用登录认证
     */
    @Value("${api-info.enable-security:true}")
    private boolean enableSecurity;

    /**
     * 文档标题
     */
    @Value("${api-info.title:}")
    private String title;

    /**
     * 文档描述
     */
    @Value("${api-info.description:api info}")
    private String description;

    /**
     * 文档版本
     */
    @Value("${api-info.version:1.0.0}")
    private String version;

    /**
     * 联系人姓名
     */
    @Value("${api-info.contact-name:z7}")
    private String contactName;

    /**
     * 联系人网址
     */
    @Value("${api-info.contact-url:https://github.com/z7-x/review-frame}")
    private String contactUrl;

    /**
     * 联系人邮箱
     */
    @Value("${api-info.contact-email:790534238@qq.com}")
    private String contactEmail;
}
