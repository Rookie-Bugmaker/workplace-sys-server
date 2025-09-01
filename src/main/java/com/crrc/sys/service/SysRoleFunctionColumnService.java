package com.crrc.sys.service;

import com.crrc.sys.entity.function.column.SysFunctionColumn;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-11-10
 */
public interface SysRoleFunctionColumnService {

	/**
	 * 根据角色ID和快编码查询未拥有的列权限
	 *
	 * @param roleId    角色ID
	 * @param blockCode 块编码
	 * @return 未拥有的列权限
	 */
	List<SysFunctionColumn> getHaveNoColumnCode(long roleId, String blockCode);
}
