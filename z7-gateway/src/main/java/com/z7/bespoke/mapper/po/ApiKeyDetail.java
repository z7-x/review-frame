package com.z7.bespoke.mapper.po;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import java.io.Serializable;

/**
 * 项目名称：review-frame
 * 类 名 称：ApiKeyDetail
 * 类 描 述：TODO 第三方接口验签密钥信息
 * 创建时间：2023/4/25 11:04 上午
 * 创 建 人：z7
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "api_key_detail")
public class ApiKeyDetail implements Serializable {

    private Integer id;

    private String companyCode;

    private String apiKey;

    private String securityKey;
}

