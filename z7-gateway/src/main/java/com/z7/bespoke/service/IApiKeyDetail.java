package com.z7.bespoke.service;

import com.z7.bespoke.mapper.po.ApiKeyDetail;
import com.z7.bespoke.mapper.po.GlobalSignatureProperties;
import com.z7.bespoke.mapper.po.RouteDefinition;

import java.util.List;

/**
 * 项目名称：review-frame
 * 类 名 称：IApiKeyDetail
 * 类 描 述：TODO
 * 创建时间：2023/4/25 1:45 下午
 * 创 建 人：z7
 */
public interface IApiKeyDetail {

    List<ApiKeyDetail> queryApiKeyDetails();

    List<RouteDefinition> queryRouteDefinitions();

    List<GlobalSignatureProperties> queryGlobalSignatureProperties();

}
