package com.z7.bespoke.controllrt;

import com.alibaba.fastjson.JSON;
import com.z7.bespoke.service.IApiKeyDetail;
import com.z7.bespoke.service.IGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 项目名称：review-frame
 * 类 名 称：GatewayController
 * 类 描 述：TODO
 * 创建时间：2023/4/24 3:42 下午
 * 创 建 人：z7
 */
//@Api(tags = "网关服务管理")
@RequestMapping("gateway")
@RestController
@RequiredArgsConstructor
public class GatewayController {

    private final IGatewayService gatewayService;
    private final IApiKeyDetail apiKeyDetail;

    @GetMapping("/reload")
//    @ApiOperation(value = "刷新网关配置资源路径")
    public String reload() {
        gatewayService.save();
        return "SUCCESS";
    }

    @GetMapping("/test")
//    @ApiOperation(value = "访问网关服务是否在线")
    public String test() {
        return "SUCCESS";
    }

    @GetMapping("/test-invoke")
//    @ApiOperation(value = "获取第三方网关路由配置信息")
    public String testInvoke() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("密钥配置:" + JSON.toJSONString(apiKeyDetail.queryApiKeyDetails()));
        stringBuffer.append("全局属性配置:" + JSON.toJSONString(apiKeyDetail.queryGlobalSignatureProperties()));
        stringBuffer.append("路由配置:" + JSON.toJSONString(apiKeyDetail.queryRouteDefinitions()));
        return stringBuffer.toString();
    }

}
