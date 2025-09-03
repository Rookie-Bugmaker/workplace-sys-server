package com.crrc.sys.model.dept.person;

import lombok.Data;

/**
 * 部门人员-数据传输对象
 *
 * @author 谭永强
 * @date 2025-09-03
 */
@Data
public class SysDeptPersonDTO {

    /**
     * 主键
     */
    private Long id;
    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 部门ID
     */
    private Long deptId;
    /**
     * 人员ID
     */
    private Long personId;
    /**
     * 职务
     */
    private String position;
    /**
     * 上级人员ID
     */
    private Long parentPersonId;
    /**
     * 人员姓名
     */
    private String personName;
    /**
     * 上级人员姓名
     */
    private String parentPersonName;
    /**
     * 员工编号
     */
    private String personNo;
}
