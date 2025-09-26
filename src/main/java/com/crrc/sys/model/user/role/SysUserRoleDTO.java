package com.crrc.sys.model.user.role;


import lombok.Data;

@Data
public class SysUserRoleDTO {

    private Long id;
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;
    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 角色编码
     */
    private String roleCode;

}
