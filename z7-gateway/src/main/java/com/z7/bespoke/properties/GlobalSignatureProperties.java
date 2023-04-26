package com.z7.bespoke.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

 /**
 * 项目名称：review-frame
 * 类 名 称：GatewayController
 * 类 描 述：TODO 第三方接口验签属性配置
 * 创建时间：2023/4/25 3:42 下午
 * 创 建 人：z7
 */
 @Data
@Component
@ConfigurationProperties(prefix = "filter.signature")
public class GlobalSignatureProperties {

    /**
     * 是否开启过滤
     */
    private boolean enabled = false;

    /**
     * 需要进行拦截的地址列表
     */
    private List<String> needFilterUrlPatterns;

    /**
     * 不需要进行拦截的地址列表，优先级高于需要拦截的配置
     */
    private List<String> noNeedFilterUrlPatterns = new ArrayList<>();

    /**
     * 签名公钥对应的header名称
     */
    private String apiKeyHeaderName = "ApiKey";

    /**
     * 签名摘要对应的header名称
     */
    private String authorizationHeaderName = "vAuthorization";

    private List<KeyDetail> keyDetails;

    @Data
    public static class KeyDetail {

        /**
         * 此apiKey颁发给了哪个公司
         */
        private String companyCode;

        /**
         * 接入认证编码
         */
        private String apiKey;

        /**
         * 此接入者对应的加密秘钥
         */
        private String securityKey;

    }

}
