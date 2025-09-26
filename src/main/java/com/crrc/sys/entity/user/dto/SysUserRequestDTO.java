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

}
