package com.z7.bespoke.frame.thirdparty.feishu.controller;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.z7.bespoke.frame.thirdparty.feishu.constant.FeiShuConstants;
import com.z7.bespoke.frame.thirdparty.feishu.constant.FeiShuParam;
import com.z7.bespoke.frame.thirdparty.feishu.handler.FeiShuEventHandler;
import com.z7.bespoke.frame.thirdparty.feishu.handler.response.FeiShuEventVerifyUrlResponse;
import com.z7.bespoke.frame.thirdparty.feishu.utils.Decrypt;
import com.z7.bespoke.frame.thirdparty.feishu.utils.Signature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;


/**
 * 项目名称：nb-inext
 * 类 名 称：EventDataController
 * 类 描 述：TODO 飞书事件数据控制器
 * 创建时间：2023/6/12 2:45 下午
 * 创 建 人：z7
 */
@Slf4j
@RestController
@RequestMapping("/feishu/callback")
public class EventDataController {

    @Resource(name = FeiShuEventHandler.FEI_SHU_EVENT_HANDLER + "")
    private FeiShuEventHandler feiShuEventHandler;

    /**
     * @param pram 开放平台向应用推送的事件消息
     * @description: 订阅流程-解密认证
     */
    @PostMapping("/verify")
    private JSONObject verifyApi(@RequestBody String pram) throws Exception {

        log.info("飞书-开放平台向应用推送的事件消息: \n{}", JSON.toJSONString(pram, SerializerFeature.PrettyFormat));

        JSONObject jsonResponse = JSON.parseObject(pram);
        String encrypt = jsonResponse.get("encrypt").toString();
        Decrypt decrypt = new Decrypt(FeiShuConstants.encryptKey);
        String decryptContent = decrypt.decrypt(encrypt);

        log.info("飞书-开放平台向应用解密后的内容: \n{}", JSON.toJSONString(decryptContent, SerializerFeature.PrettyFormat));

        return JSON.parseObject(decryptContent);
    }

    /**
     * @param headers 开放平台向应用推送的携带的请求头消息
     * @param pram    开放平台向应用推送的事件消息
     * @description: 订阅流程-签名验签、解密认证
     */
    @PostMapping(value = "/event")
    public JSONObject validApi(@RequestHeader Map<String, String> headers, @RequestBody String pram) {

        log.info("飞书-开放平台向应用推送的携带的请求头消息: \n{}", JSON.toJSONString(headers, SerializerFeature.PrettyFormat));
        log.info("飞书-开放平台向应用推送的事件消息: \n{}", JSON.toJSONString(pram, SerializerFeature.PrettyFormat));

        JSONObject jsonObject = new JSONObject();
        Boolean isEventChange = false;

        if (headers.containsKey(FeiShuConstants.X_LARK_REQUEST_TIMESTAMP) && headers.containsKey(FeiShuConstants.X_LARK_REQUEST_NONCE)
                && headers.containsKey(FeiShuConstants.X_LARK_SIGNATURE)) {

            String timestamp = headers.get(FeiShuConstants.X_LARK_REQUEST_TIMESTAMP);
            String nonce = headers.get(FeiShuConstants.X_LARK_REQUEST_NONCE);
            String signature = headers.get(FeiShuConstants.X_LARK_SIGNATURE);

            String calculateSignature = null;
            try {
                //签名验签
                calculateSignature = Signature.calculateSignature(timestamp, nonce, FeiShuConstants.encryptKey, pram);
            } catch (NoSuchAlgorithmException e) {
                log.info("飞书-签名验签校验异常:{}", e.getMessage());
                e.printStackTrace();
            }

            log.info("signature          = {} ", signature);
            log.info("calculateSignature = {} ", calculateSignature);

            Assert.isTrue(Objects.equals(signature, calculateSignature), "签名计算错误");
            isEventChange = true;
        }

        if (isEventChange) {
            //状态码200
            feiShuEventHandler.handleEvent(JSON.parseObject(pram));
        } else {
            FeiShuEventVerifyUrlResponse response = (FeiShuEventVerifyUrlResponse) feiShuEventHandler.handleEvent(JSON.parseObject(pram));
            jsonObject.put(FeiShuParam.VERIFY_URL_CHALLENGE, response.getChallenge());
        }
        log.info("应用服务端-响应飞书信息:{}", jsonObject.toJSONString());
        return jsonObject;
    }
}

