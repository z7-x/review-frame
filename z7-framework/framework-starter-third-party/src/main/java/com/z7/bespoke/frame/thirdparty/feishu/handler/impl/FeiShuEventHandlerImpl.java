package com.z7.bespoke.frame.thirdparty.feishu.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.z7.bespoke.frame.thirdparty.feishu.constant.FeiShuConstants;
import com.z7.bespoke.frame.thirdparty.feishu.constant.FeiShuEvent;
import com.z7.bespoke.frame.thirdparty.feishu.constant.FeiShuParam;
import com.z7.bespoke.frame.thirdparty.feishu.handler.FeiShuEventHandler;
import com.z7.bespoke.frame.thirdparty.feishu.handler.response.FeiShuEventResponse;
import com.z7.bespoke.frame.thirdparty.feishu.utils.Decrypt;
import com.z7.bespoke.frame.thirdparty.feishu.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 项目名称：nb-inext
 * 类 名 称：FeiShuEventHandlerImpl
 * 类 描 述：TODO
 * 创建时间：2023/6/14 10:53 上午
 * 创 建 人：z7
 */
@Primary
@Slf4j
@Service(FeiShuEventHandler.FEI_SHU_EVENT_HANDLER + "")
public class FeiShuEventHandlerImpl implements FeiShuEventHandler<FeiShuEventResponse> {

    @Override
    public FeiShuEventResponse handleEvent(JSONObject param) {

        log.info("飞书事件处理器: \n{}", JSON.toJSONString(param, SerializerFeature.PrettyFormat));

        if (param == null) {
            throw new RuntimeException("FeiShuEventHandlerImpl param 为空");
        }

        // 是否需要解密
        param = ifNeedDecode(param);

        log.info("飞书事件处理器,解密后数据信息: \n{}", JSON.toJSONString(param, SerializerFeature.PrettyFormat));

        // 判定属于什么请求
        String whatEvent = null;

        // 是不是验证接口
        if (param.containsKey(FeiShuParam.VERIFY_URL_CHALLENGE)) {
            whatEvent = FeiShuEvent.VERIFY_URL;
        } else {
            // 2.0版本
            // 目前响应事件的数据格式有2个版本，现在新接入的事件都是采用2.0版本的格式。
            // 事件返回包含schema字段，则是2.0版本；
            if (Objects.equals(param.getString(FeiShuParam.EVENT_SCHEMA), FeiShuParam.EVENT_SCHEMA_V2)) {
                JSONObject header = param.getJSONObject(FeiShuParam.EVENT_HEADER);
                if (header == null) {
                    throw new RuntimeException("事件头header == null");
                }

                log.info("飞书事件处理器,事件头header: \n{}", JSON.toJSONString(header, SerializerFeature.PrettyFormat));

                whatEvent = header.getString(FeiShuParam.Header.EVENT_TYPE);
            } else {
                // 1.0 版本
                // v1暂时接口统一调用v2版本
            }
        }

        if (whatEvent != null) {

            FeiShuEventHandler handler = SpringContextUtil.getBean(FeiShuEventHandler.getBeanId(whatEvent), FeiShuEventHandler.class);
            if (handler == null) {
                throw new RuntimeException("handler bean查找失败" + whatEvent);
            }
            return handler.handleEvent(param);
        }
        throw new RuntimeException("不支持的事件类型");
    }

    private JSONObject ifNeedDecode(JSONObject param) {

        // 判断是不是加密请求
        if (param.containsKey(FeiShuParam.VERIFY_URL_ENCRYPT)) {

            String encrypt = param.getString(FeiShuParam.VERIFY_URL_ENCRYPT);
            // 加密的属于
            String encryptKey = FeiShuConstants.encryptKey;
            if (encryptKey == null) {
                throw new RuntimeException("加密的属于, 但是本地没encryptKey");
            }
            // 设置了加密参数encryptKey
            // 未设置加密参数encryptKey不需要解密
            Decrypt decrypt = new Decrypt(encryptKey);
            try {
                String json = decrypt.decrypt(encrypt);
                return JSONObject.parseObject(json);
            } catch (Exception e) {
                throw new RuntimeException("解密challenge失败");
            }
        } else {
            return param;
        }
    }
}
