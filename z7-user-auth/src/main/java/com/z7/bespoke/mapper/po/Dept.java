package com.z7.bespoke.mapper.po;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 项目名称：z7-micro-small
 * 类 名 称：Dept
 * 类 描 述：TODO 系统配置---部门表
 * 创建时间：2023/4/10 3:25 下午
 * @author z7
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sys_dept", uniqueConstraints = {@UniqueConstraint(columnNames = {"dept_id"})})
public class Dept implements Serializable {

    @ApiModelProperty(value = "部门id",required = true)
    private Integer dept_id;
    @ApiModelProperty(value = "父级部门id",required = true)
    private Integer parent_id;
    @ApiModelProperty("年龄")
    private String ancestors;
    @ApiModelProperty(value = "部门名称",required = true)
    private String dept_name;
    @ApiModelProperty("排序")
    private Integer order_num;
    @ApiModelProperty("领导")
    private String leader;
    @ApiModelProperty("电话")
    private String phone;
    @ApiModelProperty("邮件")
    private String email;

}
