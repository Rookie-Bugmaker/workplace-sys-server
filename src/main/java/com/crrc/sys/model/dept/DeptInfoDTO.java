package com.crrc.sys.model.dept;

import com.crrc.sys.entity.company.Company;
import lombok.Data;

/**
 * @author 谭永强
 * @date 2025-09-08
 */
@Data
public class DeptInfoDTO {

    /**
     * 部门id
     */
    private Long id;
    /**
     * 组织编码
     */
    private String orgCode;
    /**
     * 父组织编码
     */
    private String parentOrgCode;

    /**
     * 部门名称
     */
    private String departmentName;

    /**
     * 部门排序
     */
    private Integer departmentOrder;

    /**
     * 部门状态 0:可用、1:禁用
     */
    private String status;

    /**
     * 所属公司id
     */
    private Long companyId;
    /**
     * 备注
     */
    private String remark;
    /**
     *公司信息
     */
    private Company company;
}
