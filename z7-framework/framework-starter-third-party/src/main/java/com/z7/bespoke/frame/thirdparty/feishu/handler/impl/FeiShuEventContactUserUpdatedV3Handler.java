package com.z7.bespoke.frame.thirdparty.feishu.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.z7.bespoke.frame.thirdparty.feishu.constant.FeiShuEvent;
import com.z7.bespoke.frame.thirdparty.feishu.handler.FeiShuEventHandler;
import com.z7.bespoke.frame.thirdparty.feishu.handler.response.FeiShuEventContactUserUpdatedV3Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 项目名称：nb-inext
 * 类 名 称：FeiShuEventContactUserUpdatedV3Handler
 * 类 描 述：TODO
 * 创建时间：2023/6/14 10:46 上午
 * 创 建 人：z7
 */
@Slf4j
@Service(FeiShuEventHandler.FEI_SHU_EVENT_HANDLER + "_" + FeiShuEvent.Contact.CONTACT_USER_UPDATED_V3)
public class FeiShuEventContactUserUpdatedV3Handler implements FeiShuEventHandler<FeiShuEventContactUserUpdatedV3Response> {

    @Override
    public FeiShuEventContactUserUpdatedV3Response handleEvent(JSONObject param) {

        log.info("飞书用户信息被修改事件: \n{}", JSON.toJSONString(param, SerializerFeature.PrettyFormat));

        return new FeiShuEventContactUserUpdatedV3Response();
    }
}
