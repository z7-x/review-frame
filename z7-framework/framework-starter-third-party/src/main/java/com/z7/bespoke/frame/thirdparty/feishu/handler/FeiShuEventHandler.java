package com.z7.bespoke.frame.thirdparty.feishu.handler;

import com.alibaba.fastjson.JSONObject;
import com.z7.bespoke.frame.thirdparty.feishu.handler.response.FeiShuEventResponse;

/**
 * 项目名称：nb-inext
 * 类 名 称：FeiShuEventHandler
 * 类 描 述：TODO
 * 创建时间：2023/6/13 3:12 下午
 * 创 建 人：z7
 */
public interface FeiShuEventHandler<R extends FeiShuEventResponse> {

    String FEI_SHU_EVENT_HANDLER = "feiShuEventHandler";

    R handleEvent(JSONObject param);

    static String getBeanId(String event) {
        return FEI_SHU_EVENT_HANDLER + "_" + event;
    }
}
