package com.crrc.sys.mapper;

import com.crrc.sys.entity.role.SysRole;
import com.crrc.sys.entity.role.SysRoleFindByPageParam;
import com.crrc.sys.entity.role.SysRoleTl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-09-07
 */
public interface SysRoleMapper {

    /**
     * 角色信息查询
     *
     * @param param 查询条件
     * @return 结果
     */
    List<SysRole> findByFuzzy(SysRoleFindByPageParam param);

    /**
     * 添加角色-国际化数据
     *
     * @param roleTlList 角色国际化数据集
     * @return 结果
     */
    int addTl(@Param("roleTlList") List<SysRoleTl> roleTlList);

    /**
     * 添加角色主表数据
     * @param role 角色信息
     * @return 结果
     */
    int addB(SysRole role);

    /**
     * 角色国际化修改
     * @param role 角色信息
     * @return 结果
     */
    int updateTl(SysRole role);

    /**
     * 角色主表修改
     * @param role 角色信息
     * @return 结果
     */
    int updateB(SysRole role);

    /**
     * 角色国际化删除
     * @param ids 角色ID
     * @return 结果
     */
    int deleteTl(@Param("ids") List<Long> ids);

    /**
     * 角色主表删除
     * @param ids 角色ID
     * @return 结果
     */
    int deleteB(@Param("ids") List<Long> ids);

    /**
     * 根据角色ID查询角色信息
     *
     * @param roleId 角色ID
     * @param language 语言
     * @return 角色信息
     */
    SysRole findById(@Param("roleId") long roleId, @Param("language") String language);

    /**
     * 根据角色编码查询角色信息
     *
     * @param roleCode 角色编码
     * @param language 语言
     * @return 结果
     */
    SysRole findByCode(@Param("roleCode") String roleCode, @Param("language") String language);

    /**
     * 查询当前用户拥有的角色列表
     *
     * @param userId 用户ID
     * @param language 语言
     * @return 角色列表
     */
    List<SysRole> findByUserId(@Param("userId") long userId, @Param("language") String language);

	/**
	 * 根据角色名称查询角色信息
	 *
	 * @param roleName 角色名称
	 * @param language 语言
	 * @return 角色信息
	 */
    SysRole findByName(@Param("roleName") String roleName, @Param("language") String language);
	/**
	 * 根据编码集合查询角色信息
	 *
	 * @param roleCodeList 编码集合
	 * @param language 语言
	 * @return 结果
	 */
    List<SysRole> findByCodes(@Param("roleCodeList") List<String> roleCodeList,@Param("language") String language);
}
