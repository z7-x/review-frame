package com.z7.bespoke.mapper;


import com.z7.bespoke.mapper.po.RouteDefinition;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

/**
 * 项目名称：review-frame
 * 类 名 称：RouteDefinitionMapper
 * 类 描 述：TODO
 * 创建时间：2023/4/25 11:10 上午
 * 创 建 人：z7
 */
@Mapper
public interface RouteDefinitionMapper extends BaseMapper<RouteDefinition> {

    /**
     * @description: 获取路由配置列表
     */
    List<RouteDefinition> queryRouteDefinitions();
}