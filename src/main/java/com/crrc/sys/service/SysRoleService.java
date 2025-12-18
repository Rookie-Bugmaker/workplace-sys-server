package com.crrc.sys.service;

import com.crrc.sys.common.PaginationGrid;
import com.crrc.sys.entity.role.SysRole;
import com.crrc.sys.entity.role.SysRoleFindByPageParam;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-09-07
 */
public interface SysRoleService {

    /**
     * 角色信息分页查询
     *
     * @param param 查询条件
     * @return 分页结果
     */
    PaginationGrid<SysRole> findByPage(SysRoleFindByPageParam param);

    List<SysRole> findList(SysRoleFindByPageParam param);

    /**
     * 新增
     *
     * @param role 角色信息
     * @return 结果
     */
    int add(SysRole role);

    /**
     * 修改
     *
     * @param role 用户信息
     * @return 结果
     */
    int update(SysRole role);

    /**
     * 删除
     *
     * @param ids 角色ID
     * @return 结果
     */
    int delete(List<Long> ids);

    /**
     * 根据角色ID查询角色信息(全量信息)
     *
     * @param roleId   角色ID
     * @param language 语言
     * @return 角色信息
     */
    SysRole findById(long roleId, String language);

    /**
     * 根据角色ID查询角色信息(精简信息)
     *
     * @param roleId   角色ID
     * @param language 语言
     * @return 角色信息
     */
    SysRole findByIdMin(long roleId, String language);

    /**
     * 根据角色编码查询角色信息
     *
     * @param roleCode 角色编码
     * @param language 语言
     * @return 结果
     */
    SysRole findByCode(String roleCode, String language);

    /**
     * 查询当前用户拥有的角色列表
     *
     * @param userId   用户ID
     * @param language 语言
     * @return 角色列表
     */
    List<SysRole> findByUserId(long userId, String language);

    /**
     * 根据角色名称查询角色信息
     *
     * @param roleName 角色名称
     * @param language 语言
     * @return 角色信息
     */
    SysRole findByName(String roleName, String language);

    /**
     * 根据编码集合查询角色信息
     *
     * @param roleCodeList 编码集合
     * @param language     语言
     * @return 结果
     */
    List<SysRole> findByCodes(List<String> roleCodeList, String language);

    /**
     * 查询所有角色ID集合
     *
     * @return 角色ID集合
     */
    List<Long> findRoleIdList();

    /**
     * 绑定用户
     *
     * @param roleId     角色ID
     * @param userIdList 用户ID集合
     * @return true:成功、false:失败
     */
    boolean bindUser(long roleId, List<Long> userIdList);
}
