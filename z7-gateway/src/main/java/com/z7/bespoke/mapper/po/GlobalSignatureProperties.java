package com.z7.bespoke.mapper.po;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * 项目名称：review-frame
 * 类 名 称：GlobalSignatureProperties
 * 类 描 述：TODO 全局签名验证属性
 * 创建时间：2023/4/25 11:04 上午
 * 创 建 人：z7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "global_signature_properties")
public class GlobalSignatureProperties implements Serializable {

    private Integer id;

    private Boolean enabled;
    /**
     * 签名公钥对应的header名称
     */
    private String apiKeyHeaderName;
    /**
     * 签名摘要对应的header名称
     */
    private String authorizationHeaderName;
    /**
     * 需要进行拦截的地址列表用英文逗号隔开例：/home/**,/work/**
     */
    private String needFilterUrlPatterns;
    /**
     * 不需要进行拦截的地址列表，优先级高于需要拦截的配置
     */
    private String noNeedFilterUrlPatterns;
}

