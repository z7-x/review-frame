package com.z7.bespoke.service.impl;

import com.z7.bespoke.mapper.DeptMapper;
import com.z7.bespoke.mapper.po.Dept;
import com.z7.bespoke.service.IDeptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 项目名称：z7-micro-small
 * 类 名 称：DeptServiceImpl
 * 类 描 述：TODO
 * 创建时间：2023/4/13 11:46 上午
 *
 * @author z7
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements IDeptService {

    private final DeptMapper deptMapper;

    @Override
    public List<Dept> getDeptAll() {
//        List<Dept> select = deptMapper.getDepts();
        List<Dept> select = deptMapper.select(Dept.builder().dept_id(100).build());
        return select;
    }

}
