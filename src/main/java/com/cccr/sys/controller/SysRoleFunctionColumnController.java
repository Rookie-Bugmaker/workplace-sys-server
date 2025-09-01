package com.cccr.sys.controller;

import com.cccr.sys.entity.function.column.SysFunctionColumn;
import com.cccr.sys.service.SysRoleFunctionColumnService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 谭永强
 * @date 2022-11-10
 */
@RestController
@RequestMapping("/role/function/column")
public class SysRoleFunctionColumnController {

	@Resource
	private SysRoleFunctionColumnService roleFunctionColumnService;

	/**
	 * 根据角色ID和快编码查询未拥有的列权限
	 *
	 * @param roleId    角色ID
	 * @param blockCode 块编码
	 * @return 未拥有的列权限
	 */
	@PostMapping("getHaveNoColumnCode")
	public List<SysFunctionColumn> getHaveNoColumnCode(long roleId, String blockCode){
		return roleFunctionColumnService.getHaveNoColumnCode(roleId, blockCode);
	}
}
