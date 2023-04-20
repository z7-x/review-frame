package com.z7.bespoke.service;

import com.z7.bespoke.mapper.po.Dept;

import java.util.List;

/**
 * 项目名称：z7-micro-small
 * 类 名 称：IDeptService
 * 类 描 述：TODO
 * 创建时间：2023/4/13 11:45 上午
 * @author z7
 */
public interface IDeptService {

    /**
     * @description: 获取所有部门列表
     */
    List<Dept> getDeptAll();
}
