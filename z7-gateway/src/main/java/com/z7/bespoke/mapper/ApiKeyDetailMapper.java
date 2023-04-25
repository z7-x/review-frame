package com.z7.bespoke.mapper;


import com.z7.bespoke.mapper.po.ApiKeyDetail;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

/**
 * 项目名称：review-frame
 * 类 名 称：ApiKeyDetailMapper
 * 类 描 述：TODO
 * 创建时间：2023/4/25 11:10 上午
 * 创 建 人：z7
 */
@Mapper
public interface ApiKeyDetailMapper extends BaseMapper<ApiKeyDetail> {

    /**
     * @description: 查询所以密钥key
     */
    List<ApiKeyDetail> queryApiKeyDetails();
}

