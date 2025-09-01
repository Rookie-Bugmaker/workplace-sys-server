package com.crrc.sys.mapper;

import com.crrc.sys.entity.role.SysRoleColumnValueSet;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-10-21
 */
public interface SysRoleColumnValueSetMapper {

	/**
	 * 添加角色菜单列值集数据
	 * @param roleMenuColumnValueSets 角色菜单列值集
	 */
	void insert(@Param("list") List<SysRoleColumnValueSet> roleMenuColumnValueSets);

	/**
	 * 删除角色菜单列值集数据
	 * @param roleIdList 角色ID
	 */
	void deleteByRoleIds(@Param("roleIdList") List<Long> roleIdList);

	/**
	 * 根据角色ID查询列权限值集
	 * @param roleId 角色ID
	 * @return 结果
	 */
	List<SysRoleColumnValueSet> findByRoleId(@Param("roleId") long roleId);

	/**
	 * 根据角色ID和块编码查询值集信息
	 *
	 * @param roleId    角色ID
	 * @param blockCode 块编码
	 * @return 结果
	 */
	List<SysRoleColumnValueSet> findByRoleIdAndBlockCode(@Param("roleId") long roleId, @Param("blockCode") String blockCode);
}
