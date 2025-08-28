package com.yunling.sys.mapper;

import com.yunling.sys.entity.role.SysRoleOrg;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-10-15
 */
public interface SysRoleOrgMapper {

    /**
     * 角色组织新增
     * @param roleOrgList 角色组织
     */
    void insert(@Param("roleOrgList") List<SysRoleOrg> roleOrgList);

    /**
     * 根据角色ID或用户ID删除角色组织中间表数据
     * @param roleId 角色ID
     */
    void deleteByRoleId(@Param("roleId") long roleId);

    /**
     * 根据角色ID删除角色组织中间表数据(批量)
     * @param roleIds 角色ID
     */
    void deleteByRoleIds(@Param("roleIds") List<Long> roleIds);
}
