package com.z7.bespoke.service.impl;

import com.z7.bespoke.mapper.ApiKeyDetailMapper;
import com.z7.bespoke.mapper.GlobalSignaturePropertiesMapper;
import com.z7.bespoke.mapper.RouteDefinitionMapper;
import com.z7.bespoke.mapper.po.ApiKeyDetail;
import com.z7.bespoke.mapper.po.GlobalSignatureProperties;
import com.z7.bespoke.mapper.po.RouteDefinition;
import com.z7.bespoke.service.IApiKeyDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 项目名称：review-frame
 * 类 名 称：ApiKeyDetailService
 * 类 描 述：TODO 接口测试
 * 创建时间：2023/4/25 1:45 下午
 * 创 建 人：z7
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ApiKeyDetailService implements IApiKeyDetail {

    private final ApiKeyDetailMapper apiKeyDetailMapper;
    private final RouteDefinitionMapper routeDefinitionMapper;
    private final GlobalSignaturePropertiesMapper globalSignaturePropertiesMapper;

    @Override
    public List<ApiKeyDetail> queryApiKeyDetails() {
        return apiKeyDetailMapper.queryApiKeyDetails();
    }

    @Override
    public List<RouteDefinition> queryRouteDefinitions() {
        return routeDefinitionMapper.queryRouteDefinitions();
    }

    @Override
    public List<GlobalSignatureProperties> queryGlobalSignatureProperties() {
        return globalSignaturePropertiesMapper.queryGlobalSignatureProperties();
    }
}
