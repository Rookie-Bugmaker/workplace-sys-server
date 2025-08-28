package com.yunling.sys.service.impl;

import com.yunling.sys.entity.role.SysRoleColumnValueSet;
import com.yunling.sys.mapper.SysRoleColumnValueSetMapper;
import com.yunling.sys.service.SysRoleColumnValueSetService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 谭永强
 * @date 2022-11-09
 */
@Service
public class SysRoleColumnValueSetServiceImpl implements SysRoleColumnValueSetService {

	@Resource
	private SysRoleColumnValueSetMapper roleColumnValueSetMapper;

	/**
	 * 根据角色ID和块编码查询值集信息
	 *
	 * @param roleId    角色ID
	 * @param blockCode 块编码
	 * @return 结果
	 */
	@Override
	public List<SysRoleColumnValueSet> findByRoleIdAndBlockCode(long roleId, String blockCode) {
		return roleColumnValueSetMapper.findByRoleIdAndBlockCode(roleId, blockCode);
	}
}
