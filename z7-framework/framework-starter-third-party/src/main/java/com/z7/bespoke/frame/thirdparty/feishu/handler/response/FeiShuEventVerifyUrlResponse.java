package com.z7.bespoke.frame.thirdparty.feishu.handler.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 项目名称：nb-inext
 * 类 名 称：FeiShuEventVerifyUrlResponse
 * 类 描 述：TODO
 * 创建时间：2023/6/13 3:24 下午
 * 创 建 人：z7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeiShuEventVerifyUrlResponse  extends FeiShuEventResponse{

    private String challenge;

}
