package com.z7.bespoke.controller;

//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 项目名称：review-frame
 * 类 名 称：DataStatsController
 * 类 描 述：TODO
 * 创建时间：2023/5/15 10:47 上午
 * 创 建 人：z7
 */
@RequestMapping("/data-stats")
@RestController
@RequiredArgsConstructor
//@Api(tags = "数据分析服务")
public class DataStatsController {

    @GetMapping("/invoke")
//    @ApiOperation(value = "调用服务是否启动成功")
    public String invoke() {
        return "数据分析服务api调用成功";
    }

}
