package com.crrc.sys.entity.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class SysUserRequestDTO {


    /**
     * 角色codes
     */
    private List<String> roleCodes;

    /**
     * 部门编码
     */
    private String orgCode;

    private String fullName;
    /**
     * 角色编码
     */
    private String roleCode;

    private List<Long> userIdList;

    private List<String> userNameList;

    /**
     * 租户id
     */
    private Long companyId;

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 公司ID列表
     */
    private List<Long> companyIds;

    /**
     * 公司orgCode列表
     */
    private List<String> companyOrgCodes;

    /**
     * 部门ID列表
     */
    private List<Long> deptIds;

    /**
     * 部门orgCode列表
     */
    private List<String> deptOrgCodes;

    /**
     * 模糊查询-用户名
     */
    private String userName;

    /**
     * 模糊查询-用户描述
     */
    private String userDescription;

    /**
     * 模糊查询-工号
     */
    private String jobNo;

    /**
     * 当前页码(分页查询,默认 1)
     */
    private Integer page;

    /**
     * 每页条数(分页查询,默认 50)
     */
    private Integer pageSize;

}
