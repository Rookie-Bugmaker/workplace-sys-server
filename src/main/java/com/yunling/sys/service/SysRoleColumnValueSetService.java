package com.yunling.sys.service;

import com.yunling.sys.entity.role.SysRoleColumnValueSet;

import java.util.List;

/**
 * @author 谭永强
 * @date 2022-11-09
 */
public interface SysRoleColumnValueSetService {

	/**
	 * 根据角色ID和块编码查询值集信息
	 *
	 * @param roleId    角色ID
	 * @param blockCode 块编码
	 * @return 结果
	 */
	List<SysRoleColumnValueSet> findByRoleIdAndBlockCode(long roleId, String blockCode);
}
