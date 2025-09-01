package com.crrc.sys.mapper;

import com.crrc.sys.entity.role.SysRoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-09-22
 */
public interface SysRoleMenuMapper {

    /**
     * 根据菜单ID删除角色菜单中间表数据
     *
     * @param menuIdList 菜单ID集合
     */
    void deleteByMenuIds(@Param("menuIdList") List<Long> menuIdList);

    /**
     * 角色菜单添加
     *
     * @param roleMenuList 角色菜单集合
     */
    void insert(@Param("roleMenuList") List<SysRoleMenu> roleMenuList);

    /**
     * 根据角色ID删除角色菜单数据
     *
     * @param roleId 角色ID
     */
    void deleteByRoleId(@Param("roleId") long roleId);

    /**
     * 根据角色ID删除角色菜单数据(批量)
     *
     * @param roleIds 角色ID
     */
    void deleteByRoleIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 根据角色ID查询拥有的菜单ID
     *
     * @param roleId 角色ID
     * @return 结果
     */
    List<SysRoleMenu> findByRoleId(@Param("roleId") long roleId);
}
