package com.z7.bespoke.frame.thirdparty.feishu.handler.request;

import lombok.Data;

/**
 * 项目名称：nb-inext
 * 类 名 称：FeiShuEvenVerifyUrlParam
 * 类 描 述：TODO
 * 创建时间：2023/6/13 4:55 下午
 * 创 建 人：z7
 */
@Data
public class FeiShuEvenVerifyUrlParam {

    /**
     * 应用需要在响应中原样返回的值
     */
    private String challenge;
    /**
     * 即 Verification Token
     */
    private String token;
    /**
     * "type": "url_verification" 表示这是一个验证请求
     */
    private String type;
}