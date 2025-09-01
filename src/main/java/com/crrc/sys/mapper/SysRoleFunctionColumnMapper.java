package com.crrc.sys.mapper;

import com.crrc.sys.entity.role.SysRoleFunctionColumn;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-11-03
 */
public interface SysRoleFunctionColumnMapper {

	/**
	 * 保存拥有的列权限
	 * @param functionColumnList 拥有列权限
	 */
	void insert(@Param("list") List<SysRoleFunctionColumn> functionColumnList);

	/**
	 * 角色列权限删除
	 * @param roleIdList 角色ID
	 */
	void deleteByRoleIds(@Param("roleIdList") List<Long> roleIdList);

	/**
	 * 根据角色ID查询拥有的列权限
	 * @param roleId 角色ID
	 * @return 结果
	 */
	List<SysRoleFunctionColumn> findByRoleId(@Param("roleId") long roleId);
}
