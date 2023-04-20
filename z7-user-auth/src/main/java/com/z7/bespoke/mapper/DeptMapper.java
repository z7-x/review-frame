package com.z7.bespoke.mapper;

import com.z7.bespoke.mapper.po.Dept;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

/**
 * 项目名称：z7-micro-small
 * 类 名 称：DeptMapper
 * 类 描 述：TODO
 * 创建时间：2023/4/13 11:22 上午
 * @author z7
 */
@Mapper
public interface DeptMapper extends BaseMapper<Dept>{

    List<Dept> getDepts();

}
