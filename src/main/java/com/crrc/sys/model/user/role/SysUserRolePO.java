package com.crrc.sys.model.user.role;

import lombok.Data;

/**
 * @author 谭永强
 * @date 2025-09-08
 */
@Data
public class SysUserRolePO {

    private Long id;
    /**
     * 用户ID
     */
    private Long userId;
    /**
     * 角色ID
     */
    private Long roleId;
}
