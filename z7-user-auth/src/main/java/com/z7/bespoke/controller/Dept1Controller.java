package com.z7.bespoke.controller;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.z7.bespoke.mapper.po.Dept;
import com.z7.bespoke.service.IDeptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：review-frame
 * 类 名 称：UserController
 * 类 描 述：TODO
 * 创建时间：2023/4/3 5:07 下午
 * @author z7
 */
@ApiSupport(order = 99)
@RequestMapping("/dept1")
@RestController
@RequiredArgsConstructor
@Api(tags = "我是DemoController---1")
public class Dept1Controller {

    private   final IDeptService deptService;

    @GetMapping("/get")
    @ApiOperation(value = "哈哈哈")
    @ApiOperationSupport(order = 3)
    public List<Dept> get() {
        return deptService.getDeptAll();
    }

}
