package com.z7.bespoke.controllrt;

import com.alibaba.fastjson.JSON;
import com.z7.bespoke.mapper.po.ApiKeyDetail;
import com.z7.bespoke.service.IApiKeyDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/get")
    public String reload() {
        return "网关服务-测试---";
    }

    @GetMapping("/get1")
    public String get1() {
        List<ApiKeyDetail> apiKeyDetails = apiKeyDetail.queryApiKeyDetails();
        String s = JSON.toJSONString(apiKeyDetails);
        return s;
    }

    @GetMapping("/get2")
    public String get2() {
        String s = JSON.toJSONString(apiKeyDetail.queryRouteDefinitions());
        return s;
    }

    @GetMapping("/get3")
    public String get3() {
        String s = JSON.toJSONString(apiKeyDetail.queryGlobalSignatureProperties());
        return s;
    }
}
