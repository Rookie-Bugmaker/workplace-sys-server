package com.crrc.sys.service;

import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.menu.SysMenu;
import com.crrc.sys.entity.menu.SysMenuDTO;
import com.crrc.sys.entity.menu.SysMenuFindByPageParam;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-09-21
 */
public interface SysMenuService {

    /**
     * 菜单信息分页查询
     *
     * @param param 查询条件
     * @return 分页结果
     */
    PaginationGrid<SysMenu> findByPage(SysMenuFindByPageParam param);

    /**
     * 菜单新增(新)
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int add(SysMenuDTO menu);

    /**
     * 菜单修改(新)
     *
     * @param menu 菜单信息
     * @return 结果
     */
    int update(SysMenuDTO menu);

    /**
     * 菜单删除
     *
     * @param ids 菜单ID集合
     * @return 结果
     */
    int delete(List<Long> ids);

    /**
     * 根据菜单ID查询菜单信息(会递归查询该菜单下的所有子菜单)
     *
     * @param menuId   菜单ID
     * @param language 语言
     * @return 结果
     */
    SysMenu findById(long menuId, String language);

    /**
     * 根据菜单ID查询菜单信息(查询最小数据)
     *
     * @param menuId   菜单ID
     * @param language 语言
     * @return 结果
     */
    SysMenu findMinById(long menuId, String language);

    /**
     * 根据菜单编码集合查询菜单信息
     *
     * @param codeList 菜单编码集合
     * @param language 语言
     * @return 结果
     */
    List<SysMenu> findByCodes(List<String> codeList, String language);

    /**
     * 查询根菜单
     *
     * @param menuId 菜单ID
     * @return 根菜单ID
     */
    long findRootId(long menuId);

    /**
     * 根据菜单ID查询所有的子菜单ID集合
     *
     * @param menuId 菜单ID
     * @return 子菜单ID集合
     */
    List<Long> findMenuIdList(long menuId);

    /**
     * 查询根菜单列表
     *
     * @param projectId 项目ID
     * @param language  语言
     * @return 结果
     */
    List<SysMenu> findRootMenu(Long projectId, String language);

    /**
     * 根据菜单ID查询结构
     *
     * @param menuId   菜单ID
     * @param language 语言
     * @return 结果
     */
    SysMenu findTreeByMenuId(long menuId, String language);

    /**
     * 查询角色拥有的菜单(树结构)
     *
     * @param roleId   角色ID
     * @param language 语言
     * @return 结果
     */
    SysMenu findTreeByRoleId(long roleId, String language);

    /**
     * 根据菜单ID查询功能ID集合
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    List<Long> findFunctionIdByMenuId(long menuId);
}