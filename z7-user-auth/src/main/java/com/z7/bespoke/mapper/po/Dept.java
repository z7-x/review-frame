package com.z7.bespoke.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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

    private Integer dept_id;
    private Integer parent_id;
    private String ancestors;
    private String dept_name;
    private Integer order_num;
    private String leader;
    private String phone;
    private String email;

}
