package com.crrc.sys.service.impl;

import com.crrc.sys.entity.role.SysRoleColumnValueSet;
import com.crrc.sys.mapper.mapperImpl.SysRoleColumnValueSetMapperImpl;
import com.crrc.sys.service.SysRoleColumnValueSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 谭永强
 * @date 2022-11-09
 */
@Service
public class SysRoleColumnValueSetServiceImpl implements SysRoleColumnValueSetService {

	@Autowired
	private SysRoleColumnValueSetMapperImpl roleColumnValueSetMapper;

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
