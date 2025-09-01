package com.crrc.sys.service.impl;

import com.crrc.sys.entity.function.column.SysFunctionColumn;
import com.crrc.sys.mapper.SysFunctionColumnMapper;
import com.crrc.sys.service.SysRoleFunctionColumnService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 谭永强
 * @date 2022-11-10
 */
@Service
public class SysRoleFunctionColumnServiceImpl implements SysRoleFunctionColumnService {

	@Resource
	private SysFunctionColumnMapper functionColumnMapper;

	/**
	 * 根据角色ID和快编码查询未拥有的列权限
	 *
	 * @param roleId    角色ID
	 * @param blockCode 块编码
	 * @return 未拥有的列权限
	 */
	@Override
	public List<SysFunctionColumn> getHaveNoColumnCode(long roleId, String blockCode) {
		return functionColumnMapper.getHaveNoColumnCode(roleId, blockCode);
	}
}
