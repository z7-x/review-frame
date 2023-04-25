package com.z7.bespoke.controllrt;

import com.alibaba.fastjson.JSON;
import com.z7.bespoke.service.IApiKeyDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 项目名称：review-frame
 * 类 名 称：GatewayController
 * 类 描 述：TODO
 * 创建时间：2023/4/24 3:42 下午
 * 创 建 人：z7
 */
@RestController
@RequiredArgsConstructor
public class GatewayController {

    private final IApiKeyDetail apiKeyDetail;

    @GetMapping("/test")
    public String test() {
        return "SUCCESS";
    }

    @GetMapping("/test-invoke")
    public String testInvoke() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("密钥配置:" + JSON.toJSONString(apiKeyDetail.queryApiKeyDetails()));
        stringBuilder.append("\n\n\n\n");
        stringBuilder.append("全局属性配置:" + JSON.toJSONString(apiKeyDetail.queryGlobalSignatureProperties()));
        stringBuilder.append("\n\n\n\n");
        stringBuilder.append("路由配置:" + JSON.toJSONString(apiKeyDetail.queryRouteDefinitions()));
        return stringBuilder.toString();
    }

}
