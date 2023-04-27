package com.z7.bespoke.frame.swagger.support;

/**
 * 项目名称：review-frame
 * 类 名 称：EnumDescriptor
 * 类 描 述：TODO 枚举说明器
 * 创建时间：2023/4/27 10:24 上午
 * 创 建 人：z7
 */
public interface EnumDescriptor {

    /**
     * 获取枚举项说明
     *
     * @return 枚举项说明
     */
    String obtainDescription();
}