package com.z7.bespoke.frame.thirdparty.feishu.handler.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 项目名称：nb-inext
 * 类 名 称：FeiShuEventResponse
 * 类 描 述：TODO
 * 创建时间：2023/6/13 3:23 下午
 * 创 建 人：z7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeiShuEventResponse implements Serializable {

    private String msg;

}
