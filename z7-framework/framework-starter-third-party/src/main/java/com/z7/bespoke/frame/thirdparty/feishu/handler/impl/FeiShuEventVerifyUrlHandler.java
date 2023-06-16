package com.z7.bespoke.frame.thirdparty.feishu.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.z7.bespoke.frame.thirdparty.feishu.constant.FeiShuConstants;
import com.z7.bespoke.frame.thirdparty.feishu.constant.FeiShuEvent;
import com.z7.bespoke.frame.thirdparty.feishu.handler.FeiShuEventHandler;
import com.z7.bespoke.frame.thirdparty.feishu.handler.request.FeiShuEvenVerifyUrlParam;
import com.z7.bespoke.frame.thirdparty.feishu.handler.response.FeiShuEventVerifyUrlResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 项目名称：nb-inext
 * 类 名 称：FeiShuEventVerifyUrlHandler
 * 类 描 述：TODO 用于飞书验证服务器url请求
 * 创建时间：2023/6/14 10:25 上午
 * 创 建 人：z7
 */
@Slf4j
@Service(FeiShuEventHandler.FEI_SHU_EVENT_HANDLER + "_" + FeiShuEvent.VERIFY_URL)
public class FeiShuEventVerifyUrlHandler implements FeiShuEventHandler<FeiShuEventVerifyUrlResponse> {

    @Override
    public FeiShuEventVerifyUrlResponse handleEvent(JSONObject param) {

        log.info("飞书验证服务器URL请求: \n{}", JSON.toJSONString(param, SerializerFeature.PrettyFormat));

        FeiShuEvenVerifyUrlParam feishuEvenVerifyUrlParam = JSONObject.toJavaObject(param, FeiShuEvenVerifyUrlParam.class);

        String verificationToken = FeiShuConstants.verificationToken;

        String token = feishuEvenVerifyUrlParam.getToken();
        String challenge = feishuEvenVerifyUrlParam.getChallenge();

        if (!Objects.equals(token, verificationToken)) {
            log.info("飞书校验token失败:token          = {}", token);
            log.info("飞书校验token失败:challenge      = {}", challenge);
            throw new RuntimeException("token校验失败");
        }
        return new FeiShuEventVerifyUrlResponse(challenge);
    }
}
