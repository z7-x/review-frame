package com.z7.bespoke.controller;

import com.z7.bespoke.mapper.po.Dept;
import com.z7.bespoke.service.IDeptService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 项目名称：review-frame
 * 类 名 称：UserController
 * 类 描 述：TODO
 * 创建时间：2023/4/3 5:07 下午
 * @author z7
 */
@RestController
@RequiredArgsConstructor
public class DeptController {

    private   final IDeptService deptService;

    @GetMapping("/get")
    public List<Dept> get() {
        return deptService.getDeptAll();
    }
}
