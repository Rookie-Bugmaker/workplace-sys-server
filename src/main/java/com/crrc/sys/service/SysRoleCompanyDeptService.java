package com.crrc.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crrc.sys.entity.role.SysRoleCompanyDept;

/**
 * @author 谭永强
 * @date 2025-09-10
 */
public interface SysRoleCompanyDeptService extends IService<SysRoleCompanyDept> {

    /**
     * 根据角色删除角色部门数据
     *
     * @param roleId 角色ID
     */
    void removeByRoleId(Long roleId);
}
